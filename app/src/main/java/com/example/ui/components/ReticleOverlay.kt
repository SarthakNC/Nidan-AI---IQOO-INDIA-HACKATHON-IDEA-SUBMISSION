package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryDark
import com.example.ui.theme.TriageGreen
import com.example.ui.theme.TriageRed
import com.example.ui.theme.TriageYellow

@Composable
fun CameraScanningViewport(
    guideType: CameraGuideType,
    isAnalyzing: Boolean,
    modifier: Modifier = Modifier,
    overlayText: String = "Position within guide frame"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLineFloat"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF121A1C))
    ) {
        val width = maxWidth
        val height = maxHeight

        // Viewfinder reticle canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Corner brackets
            val bracketLen = 36.dp.toPx()
            val bracketStroke = 3.5f.dp.toPx()
            val margin = 28.dp.toPx()
            val bracketColor = Color(0xFF4FD8EB)

            // Top-Left
            drawLine(bracketColor, Offset(margin, margin), Offset(margin + bracketLen, margin), bracketStroke)
            drawLine(bracketColor, Offset(margin, margin), Offset(margin, margin + bracketLen), bracketStroke)

            // Top-Right
            drawLine(bracketColor, Offset(canvasW - margin, margin), Offset(canvasW - margin - bracketLen, margin), bracketStroke)
            drawLine(bracketColor, Offset(canvasW - margin, margin), Offset(canvasW - margin, margin + bracketLen), bracketStroke)

            // Bottom-Left
            drawLine(bracketColor, Offset(margin, canvasH - margin), Offset(margin + bracketLen, canvasH - margin), bracketStroke)
            drawLine(bracketColor, Offset(margin, canvasH - margin), Offset(margin, canvasH - margin - bracketLen), bracketStroke)

            // Bottom-Right
            drawLine(bracketColor, Offset(canvasW - margin, canvasH - margin), Offset(canvasW - margin - bracketLen, canvasH - margin), bracketStroke)
            drawLine(bracketColor, Offset(canvasW - margin, canvasH - margin), Offset(canvasW - margin, canvasH - margin - bracketLen), bracketStroke)

            // Center Guide Box
            val boxW = canvasW * 0.72f
            val boxH = canvasH * 0.52f
            val boxL = (canvasW - boxW) / 2f
            val boxT = (canvasH - boxH) / 2f

            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(boxL, boxT),
                size = Size(boxW, boxH),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
            )

            // Laser scanline if analyzing
            if (isAnalyzing) {
                val lineY = boxT + (boxH * scanLineProgress)
                drawLine(
                    color = Color(0xFF2DD4BF),
                    start = Offset(boxL + 8.dp.toPx(), lineY),
                    end = Offset(boxL + boxW - 8.dp.toPx(), lineY),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        // Center Guide Graphic (Eye or MUAC visual icon overlay)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (guideType) {
                CameraGuideType.EYELID_ANEMIA -> {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Eyelid Guide",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lower Palpebral Conjunctiva",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                CameraGuideType.MUAC_TAPE -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(width = 36.dp, height = 18.dp).clip(RoundedCornerShape(4.dp)).background(TriageRed))
                        Box(modifier = Modifier.size(width = 36.dp, height = 18.dp).clip(RoundedCornerShape(4.dp)).background(TriageYellow))
                        Box(modifier = Modifier.size(width = 44.dp, height = 18.dp).clip(RoundedCornerShape(4.dp)).background(TriageGreen))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Align MUAC Tape Color Window",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Top Status Pill
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAnalyzing) Color(0xFF2DD4BF) else Color(0xFFE2E8F0))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAnalyzing) "Processing on Hexagon NPU..." else overlayText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Lighting Indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Lighting Indicator",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Good ambient lighting improves screening quality",
                    color = Color(0xFFF1F5F9),
                    fontSize = 11.sp
                )
            }
        }
    }
}

enum class CameraGuideType {
    EYELID_ANEMIA,
    MUAC_TAPE
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ScreeningRecord
import com.example.domain.model.TriageLevel
import com.example.domain.triage.TriageRules
import com.example.ui.theme.ClinicalOutline
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTextMuted
import com.example.ui.theme.TealIconBg
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActionLauncherCard(
    title: String,
    hindiTitle: String,
    description: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimaryHighlight: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimaryHighlight) TealPrimary else ClinicalSurface
        ),
        border = if (isPrimaryHighlight) null else BorderStroke(1.dp, ClinicalOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPrimaryHighlight) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPrimaryHighlight) Color.White.copy(alpha = 0.15f) else iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isPrimaryHighlight) Color.White else iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrimaryHighlight) Color.White else Color(0xFF191C1B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = hindiTitle,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isPrimaryHighlight) Color.White.copy(alpha = 0.8f) else ClinicalTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (isPrimaryHighlight) Color.White.copy(alpha = 0.85f) else Color(0xFF3F4948),
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = if (isPrimaryHighlight) Color.White.copy(alpha = 0.8f) else ClinicalTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RecentScreeningItemCard(
    record: ScreeningRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
    val initials = record.patientName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "PT" }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
        border = BorderStroke(1.dp, ClinicalOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TealIconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.patientName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Age ${record.patientAge}",
                        fontSize = 11.5.sp,
                        color = ClinicalTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = record.screeningType.displayName,
                    fontSize = 11.5.sp,
                    color = ClinicalTextMuted
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                TriageBadge(level = record.triageLevel)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = ClinicalTextMuted
                )
            }
        }
    }
}

@Composable
fun ClinicalSafetyNoticeBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F4F3))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical Safety Note",
                tint = TealPrimary,
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = TriageRules.CLINICAL_SAFETY_DISCLAIMER,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color(0xFF3F4948),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


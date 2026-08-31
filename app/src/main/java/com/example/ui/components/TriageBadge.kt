package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.TriageLevel
import com.example.ui.theme.TriageGreen
import com.example.ui.theme.TriageGreenLight
import com.example.ui.theme.TriageGreenText
import com.example.ui.theme.TriageRed
import com.example.ui.theme.TriageRedLight
import com.example.ui.theme.TriageRedText
import com.example.ui.theme.TriageYellow
import com.example.ui.theme.TriageYellowLight
import com.example.ui.theme.TriageYellowText

@Composable
fun TriageBadge(
    level: TriageLevel,
    modifier: Modifier = Modifier,
    showAction: Boolean = false
) {
    val (bgColor, dotColor, textColor) = when (level) {
        TriageLevel.GREEN -> Triple(TriageGreenLight, TriageGreen, TriageGreenText)
        TriageLevel.YELLOW -> Triple(TriageYellowLight, TriageYellow, TriageYellowText)
        TriageLevel.RED -> Triple(TriageRedLight, TriageRed, TriageRedText)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = when (level) {
                    TriageLevel.RED -> "RED RISK"
                    TriageLevel.YELLOW -> "YELLOW RISK"
                    TriageLevel.GREEN -> "GREEN"
                },
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}


package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OfflineBadgeBg
import com.example.ui.theme.OfflineBadgeDot
import com.example.ui.theme.OfflineBadgeText
import com.example.ui.theme.TealPrimary

@Composable
fun OfflineStatusBar(
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(OfflineBadgeBg)
            .padding(horizontal = 14.dp, vertical = if (isCompact) 6.dp else 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(OfflineBadgeDot)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OFFLINE MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OfflineBadgeText,
                    letterSpacing = 0.6.sp
                )
                if (!isCompact) {
                    Text(
                        text = " • On-Device AI Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = OfflineBadgeText.copy(alpha = 0.8f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypted Local Storage",
                tint = OfflineBadgeDot,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Local Encrypted",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = OfflineBadgeText
            )
        }
    }
}


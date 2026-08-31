package com.example.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.ScreeningRecord
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.RecentScreeningItemCard
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalOutline
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.ClinicalTextMuted
import com.example.ui.theme.TealIconBg
import com.example.ui.theme.TealMintLight
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import com.example.ui.theme.TriageRed
import com.example.ui.theme.TriageRedLight
import com.example.ui.theme.TriageYellow
import com.example.ui.theme.TriageYellowLight

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToNewPatient: () -> Unit,
    onNavigateToAnemia: (patientId: String) -> Unit,
    onNavigateToMuac: (patientId: String) -> Unit,
    onNavigateToVoice: (patientId: String) -> Unit,
    onNavigateToHandover: () -> Unit,
    onNavigateToPatientProfile: (patientId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val recentScreenings by viewModel.recentScreenings.collectAsStateWithLifecycle()
    val todayCount by viewModel.todayCount.collectAsStateWithLifecycle()
    val pendingSync by viewModel.pendingSync.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ClinicalBackground)
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Geometric Balance Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Nidan AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00201F),
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "निदान AI • OFFLINE ASSISTANT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F4948),
                        letterSpacing = 0.8.sp
                    )
                }

                // Geometric Offline Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(TealMintLight)
                        .clickable { onNavigateToHandover() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("home_handover_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TealPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (pendingSync > 0) "OFFLINE • $pendingSync PENDING" else "OFFLINE MODE",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00201F),
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }
        }

        // Geometric Balance Hero Card (Mint Container #CCE8E3)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_screenings_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    // Decorative geometric background circle
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .offset(x = 240.dp, y = 20.dp)
                            .clip(CircleShape)
                            .background(TealPrimary.copy(alpha = 0.07f))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Today's Field Work",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF00504F).copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (todayCount > 0) todayCount.toString() else "12",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00201F)
                            )
                            Text(
                                text = "Completed field screenings",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF00504F)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(TealPrimary)
                                .clickable { onNavigateToNewPatient() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Screening",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Geometric 2x2 Grid of Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Anemia Scan & Child MUAC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GeometricActionTile(
                        title = "Anemia Scan",
                        description = "Visual conjunctival pallor screening",
                        icon = Icons.Default.Visibility,
                        iconBg = TealIconBg,
                        iconTint = TealPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_anemia_scan"),
                        onClick = { onNavigateToAnemia("P-1024") }
                    )

                    GeometricActionTile(
                        title = "Child MUAC",
                        description = "Arm circumference nutrition risk",
                        icon = Icons.Default.ChildCare,
                        iconBg = TriageRedLight.copy(alpha = 0.7f),
                        iconTint = TriageRed,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_muac_scan"),
                        onClick = { onNavigateToMuac("P-1026") }
                    )
                }

                // Row 2: Voice Triage & New Patient (Highlight Tile)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GeometricActionTile(
                        title = "Voice Triage",
                        description = "AI transcript of local symptoms",
                        icon = Icons.Default.Mic,
                        iconBg = TriageYellowLight,
                        iconTint = TriageYellow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_voice_triage"),
                        onClick = { onNavigateToVoice("P-1025") }
                    )

                    GeometricActionTile(
                        title = "New Patient",
                        description = "Register and start field screening",
                        icon = Icons.Default.PersonAdd,
                        iconBg = Color.White.copy(alpha = 0.2f),
                        iconTint = Color.White,
                        isHighlight = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_new_patient"),
                        onClick = onNavigateToNewPatient
                    )
                }
            }
        }

        // Recent Screenings Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Screenings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1B)
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.clickable { onNavigateToHandover() }
                )
            }
        }

        if (recentScreenings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    border = BorderStroke(1.dp, ClinicalOutline)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No screenings recorded yet. Tap an action above to start.",
                            fontSize = 13.sp,
                            color = ClinicalTextMuted
                        )
                    }
                }
            }
        } else {
            items(recentScreenings.take(4)) { record ->
                RecentScreeningItemCard(
                    record = record,
                    onClick = { onNavigateToPatientProfile(record.patientId) },
                    modifier = Modifier.testTag("recent_item_${record.id}")
                )
            }
        }

        // Mandatory Safety Disclaimer
        item {
            Spacer(modifier = Modifier.height(4.dp))
            ClinicalSafetyNoticeBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun GeometricActionTile(
    title: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) TealPrimary else ClinicalSurface
        ),
        border = if (isHighlight) null else BorderStroke(1.dp, ClinicalOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlight) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighlight) Color.White else Color(0xFF191C1B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp,
                    color = if (isHighlight) Color.White.copy(alpha = 0.85f) else Color(0xFF3F4948),
                    maxLines = 2
                )
            }
        }
    }
}


package com.example.ui.screens.muac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CameraGuideType
import com.example.ui.components.CameraScanningViewport
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.TriageBadge
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import com.example.ui.theme.TriageGreen
import com.example.ui.theme.TriageRed
import com.example.ui.theme.TriageYellow

@Composable
fun MuacScanScreen(
    viewModel: MuacScanViewModel,
    onNavigateBack: () -> Unit,
    onScreeningSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ClinicalSurface)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("muac_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Child Nutrition Screening (MUAC)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "${state.patient?.name ?: "Child"} (${state.patient?.age ?: 4}y ${state.patient?.gender ?: ""})",
                        fontSize = 12.sp,
                        color = Color(0xFF3F4948)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("muac_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OfflineStatusBar(isCompact = true)
            }

            if (state.step == MuacScanStep.VIEWFINDER || state.step == MuacScanStep.ANALYZING) {
                // Instructions Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Instructions (निर्देश):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Place the MUAC tape around the child's left upper arm (midpoint between shoulder and elbow). Hold phone 20-30cm away.",
                                fontSize = 13.5.sp,
                                color = Color(0xFF191C1B),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Camera Viewport Reticle
                item {
                    CameraScanningViewport(
                        guideType = CameraGuideType.MUAC_TAPE,
                        isAnalyzing = state.step == MuacScanStep.ANALYZING,
                        overlayText = if (state.step == MuacScanStep.ANALYZING) "Detecting Tape Color Band..." else "Align MUAC Tape Color Window",
                        modifier = Modifier.testTag("muac_camera_viewport")
                    )
                }

                // Simulated Tape Zone Selector for Testing / Hackathon demo
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Test Case MUAC Zone Selector:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = state.selectedSimulatedZone == "RED",
                                onClick = { viewModel.selectSimulatedZone("RED") },
                                label = { Text("RED (<115mm)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TriageRed.copy(alpha = 0.2f),
                                    selectedLabelColor = TriageRed
                                ),
                                modifier = Modifier.testTag("select_muac_red")
                            )
                            FilterChip(
                                selected = state.selectedSimulatedZone == "YELLOW",
                                onClick = { viewModel.selectSimulatedZone("YELLOW") },
                                label = { Text("YELLOW (115-125)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TriageYellow.copy(alpha = 0.2f),
                                    selectedLabelColor = TriageYellow
                                ),
                                modifier = Modifier.testTag("select_muac_yellow")
                            )
                            FilterChip(
                                selected = state.selectedSimulatedZone == "GREEN",
                                onClick = { viewModel.selectSimulatedZone("GREEN") },
                                label = { Text("GREEN (>125mm)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TriageGreen.copy(alpha = 0.2f),
                                    selectedLabelColor = TriageGreen
                                ),
                                modifier = Modifier.testTag("select_muac_green")
                            )
                        }
                    }
                }

                // Capture Button
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { viewModel.captureAndAnalyze() },
                        enabled = state.step != MuacScanStep.ANALYZING,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("muac_capture_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        if (state.step == MuacScanStep.ANALYZING) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analyzing locally on device...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Capture")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan MUAC Tape", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (state.step == MuacScanStep.RESULT && state.triageResult != null) {
                val triage = state.triageResult!!
                val finding = state.finding

                // MUAC Result Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("muac_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MUAC NUTRITIONAL SCREENING",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3F4948),
                                    letterSpacing = 0.5.sp
                                )
                                TriageBadge(level = triage.level)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = triage.title,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (finding?.muacColorZone) {
                                                "RED" -> TriageRed.copy(alpha = 0.15f)
                                                "YELLOW" -> TriageYellow.copy(alpha = 0.15f)
                                                else -> TriageGreen.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "Zone: ${finding?.muacColorZone}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = when (finding?.muacColorZone) {
                                            "RED" -> TriageRed
                                            "YELLOW" -> TriageYellow
                                            else -> TriageGreen
                                        }
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(TealPrimaryContainer.copy(alpha = 0.4f))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "${finding?.muacMeasurementMm ?: 110} mm",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TealPrimary
                                    )
                                }

                                Text(
                                    text = "Conf: ${triage.confidence}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F4948),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Clinical Findings:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                            finding?.observations?.forEach { obs ->
                                Text(
                                    text = "• $obs",
                                    fontSize = 13.sp,
                                    color = Color(0xFF3F4948),
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Recommended Action Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when (triage.level) {
                                            com.example.domain.model.TriageLevel.RED -> TriageRed.copy(alpha = 0.12f)
                                            com.example.domain.model.TriageLevel.YELLOW -> TriageYellow.copy(alpha = 0.12f)
                                            else -> TealPrimaryContainer.copy(alpha = 0.35f)
                                        }
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Protocol Action Required:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (triage.level) {
                                            com.example.domain.model.TriageLevel.RED -> TriageRed
                                            com.example.domain.model.TriageLevel.YELLOW -> TriageYellow
                                            else -> TealPrimary
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = triage.recommendedAction,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF191C1B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Save / Retake Actions
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.saveScreening(onScreeningSaved) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("muac_save_screening_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Screening Record", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.retake() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("muac_retake_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retake")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retake Scan", fontSize = 15.sp)
                        }
                    }
                }
            }

            item {
                ClinicalSafetyNoticeBanner()
            }
        }
    }
}

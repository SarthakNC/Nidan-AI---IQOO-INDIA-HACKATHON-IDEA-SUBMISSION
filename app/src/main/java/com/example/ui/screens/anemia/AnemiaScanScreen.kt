package com.example.ui.screens.anemia

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import com.example.domain.model.TriageLevel
import com.example.ui.components.CameraGuideType
import com.example.ui.components.CameraScanningViewport
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.TriageBadge
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

@Composable
fun AnemiaScanScreen(
    viewModel: AnemiaScanViewModel,
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
                    modifier = Modifier.testTag("anemia_back_button")
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
                        text = "Anemia Risk Screening",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "${state.patient?.name ?: "Patient"} (${state.patient?.patientId ?: ""})",
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
                .testTag("anemia_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OfflineStatusBar(isCompact = true)
            }

            if (state.step == AnemiaScanStep.VIEWFINDER || state.step == AnemiaScanStep.ANALYZING) {
                // Instructions
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
                                text = "Position the lower eyelid inside the guide. Gently pull down lower eyelid to expose conjunctiva.",
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
                        guideType = CameraGuideType.EYELID_ANEMIA,
                        isAnalyzing = state.step == AnemiaScanStep.ANALYZING,
                        overlayText = if (state.step == AnemiaScanStep.ANALYZING) "Analyzing on Hexagon NPU..." else "Align Lower Palpebral Conjunctiva",
                        modifier = Modifier.testTag("anemia_camera_viewport")
                    )
                }

                // Simulation Selector Chips for Judge Demo
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Test Case Pattern:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3F4948)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = state.simulatePallor,
                                onClick = { viewModel.toggleSimulatedPallor(true) },
                                label = { Text("Pallor (Yellow)") },
                                modifier = Modifier.testTag("test_case_pallor_yes")
                            )
                            FilterChip(
                                selected = !state.simulatePallor,
                                onClick = { viewModel.toggleSimulatedPallor(false) },
                                label = { Text("Normal (Green)") },
                                modifier = Modifier.testTag("test_case_pallor_no")
                            )
                        }
                    }
                }

                // Capture Button
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { viewModel.captureAndAnalyze() },
                        enabled = state.step != AnemiaScanStep.ANALYZING,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("anemia_capture_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        if (state.step == AnemiaScanStep.ANALYZING) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analyzing locally on device...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Capture")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Capture & Analyze", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (state.step == AnemiaScanStep.RESULT && state.triageResult != null) {
                val triage = state.triageResult!!
                val finding = state.finding

                // Result Summary Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("anemia_result_card"),
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
                                    text = "ANEMIA RISK SCREENING",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3F4948),
                                    letterSpacing = 0.5.sp
                                )
                                TriageBadge(level = triage.level)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (finding?.conjunctivalPallorDetected == true) "Anemia Risk Detected (Moderate)" else "Low Visible Anemia Risk",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Confidence: ${triage.confidence}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TealPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• Local NPU Vision Engine",
                                    fontSize = 12.sp,
                                    color = Color(0xFF3F4948)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Observed Indicators:",
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

                            Spacer(modifier = Modifier.height(12.dp))

                            // Recommended Action Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TealPrimaryContainer.copy(alpha = 0.35f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Recommended Action:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary
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

                // Action Buttons: Save Screening / Retake
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.saveScreening(onScreeningSaved) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("anemia_save_screening_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Screening", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.retake() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("anemia_retake_button"),
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

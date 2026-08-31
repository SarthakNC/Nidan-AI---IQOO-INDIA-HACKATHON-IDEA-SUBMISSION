package com.example.ui.screens.voice

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.TriageBadge
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

@Composable
fun VoiceTriageScreen(
    viewModel: VoiceTriageViewModel,
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
                    modifier = Modifier.testTag("voice_back_button")
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
                        text = "Voice Triage (आवाज से जांच)",
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
                .testTag("voice_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OfflineStatusBar(isCompact = true)
            }

            // Language Selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Audio Language:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            Pair("hi-IN", "हिन्दी (Hindi)"),
                            Pair("en-IN", "English / Hinglish"),
                            Pair("mr-IN", "मराठी (Marathi)")
                        ).forEach { (code, label) ->
                            FilterChip(
                                selected = state.languageCode == code,
                                onClick = { viewModel.setLanguage(code) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TealPrimaryContainer,
                                    selectedLabelColor = TealPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Interactive Voice Recording Mic Pod
            if (state.state != VoiceTriageState.RESULT) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Describe what you observed.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (state.state) {
                                    VoiceTriageState.IDLE -> "Tap microphone or choose quick voice scenario below"
                                    VoiceTriageState.LISTENING -> "Listening to speech..."
                                    VoiceTriageState.PROCESSING -> "Extracting symptoms on Hexagon NPU..."
                                    else -> ""
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Waveform
                            VoiceWaveformVisualizer(
                                isListening = state.state == VoiceTriageState.LISTENING
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Big Mic Button (touch target >= 72dp)
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (state.state) {
                                            VoiceTriageState.LISTENING -> Color(0xFFEF4444)
                                            VoiceTriageState.PROCESSING -> Color(0xFFF59E0B)
                                            else -> TealPrimary
                                        }
                                    )
                                    .clickable { viewModel.toggleListening() }
                                    .testTag("voice_mic_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.state == VoiceTriageState.PROCESSING) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
                                } else {
                                    Icon(
                                        imageVector = if (state.state == VoiceTriageState.LISTENING) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = "Microphone",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when (state.state) {
                                    VoiceTriageState.IDLE -> "Tap to Speak (बोलने के लिए दबाएं)"
                                    VoiceTriageState.LISTENING -> "Recording... Tap to stop"
                                    VoiceTriageState.PROCESSING -> "Analyzing locally on device..."
                                    else -> ""
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Quick Voice Case Scenarios for Hackathon Demo
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚡ Quick Voice Case Simulations:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Moderate case
                            Card(
                                onClick = {
                                    viewModel.startListeningWithPreset(
                                        "मरीज को तीन दिनों से तेज बुखार है और जोड़ों में तेज दर्द है, भूख भी नहीं लग रही है।"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                                modifier = Modifier.testTag("voice_preset_fever")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "1. \"तेज बुखार और जोड़ों में दर्द, ३ दिन से\"",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF191C1B),
                                        modifier = Modifier.weight(1f)
                                    )
                                    TriageBadge(level = com.example.domain.model.TriageLevel.YELLOW)
                                }
                            }

                            // High Risk / Red flag case
                            Card(
                                onClick = {
                                    viewModel.startListeningWithPreset(
                                        "Child has high fever for four days, severe chest indrawing, rapid breathing and cannot drink liquids."
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                                modifier = Modifier.testTag("voice_preset_breathing")
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "2. \"Fever 4 days, chest indrawing, breathing difficulty\"",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF191C1B),
                                        modifier = Modifier.weight(1f)
                                    )
                                    TriageBadge(level = com.example.domain.model.TriageLevel.RED)
                                }
                            }
                        }
                    }
                }
            } else if (state.state == VoiceTriageState.RESULT && state.triageResult != null) {
                val triage = state.triageResult!!
                val finding = state.finding

                // Triage Evaluation Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_result_card"),
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
                                    text = "VOICE CLINICAL TRIAGE",
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Transcript Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Raw Voice Transcript (स्थानीय आवाज पहचान):",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3F4948)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${state.transcript}\"",
                                        fontSize = 13.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = Color(0xFF191C1B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Extracted Symptoms Tags
                            Text(
                                text = "Extracted Clinical Symptoms:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                finding?.symptoms?.forEach { symptom ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(TealPrimaryContainer.copy(alpha = 0.5f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = symptom,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TealPrimary
                                        )
                                    }
                                }
                                finding?.durationDays?.let { days ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFE2E8F0))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Duration: $days days",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }
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
                                        text = "Protocol Action Required:",
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

                // Worker Editable Notes
                item {
                    OutlinedTextField(
                        value = state.workerEditableNotes,
                        onValueChange = { viewModel.updateWorkerNotes(it) },
                        label = { Text("Frontline Worker Notes (टिप्पणी)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voice_notes_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ClinicalSurface,
                            unfocusedContainerColor = ClinicalSurface
                        ),
                        maxLines = 3
                    )
                }

                // Save & Retake Buttons
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.saveScreening(onScreeningSaved) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("voice_save_screening_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Voice Screening", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.reset() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("voice_record_again_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Record Again")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Record Again", fontSize = 15.sp)
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

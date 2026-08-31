package com.example.ui.screens.settings

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import com.example.ui.theme.TriageGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var languageExpanded by remember { mutableStateOf(false) }
    val languages = listOf("हिन्दी (Hindi)", "English", "मराठी (Marathi)", "বাংলা (Bengali)", "தமிழ் (Tamil)", "తెలుగు (Telugu)")

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("settings_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Settings & On-Device AI",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "Hardware Acceleration, Privacy & Regional Configuration",
                        fontSize = 13.sp,
                        color = Color(0xFF3F4948)
                    )
                }
            }

            item {
                OfflineStatusBar(isCompact = true)
            }

            // Language Selector Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = "Language", tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "App & Speech Language",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = languageExpanded,
                            onExpandedChange = { languageExpanded = !languageExpanded }
                        ) {
                            OutlinedTextField(
                                value = state.selectedLanguage,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("dropdown_language"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = languageExpanded,
                                onDismissRequest = { languageExpanded = false }
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang, color = Color(0xFF191C1B)) },
                                        onClick = {
                                            viewModel.setLanguage(lang)
                                            languageExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // On-Device AI Models Status
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SmartToy, contentDescription = "AI Models", tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "On-Device AI Inference Models",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        listOf(
                            Triple("Eyelid & MUAC Vision (INT8)", "MobileNet-V4 Custom ROI Segmentation", "42.5 MB • Active"),
                            Triple("Speech Recognition ASR (INT8)", "Whisper-tiny Multilingual Indian English/Hindi", "38.0 MB • Active"),
                            Triple("Clinical Entity Reasoner (INT8)", "Deterministic Parser + Local Medical SLM", "54.0 MB • Active")
                        ).forEach { (name, desc, size) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C1B))
                                    Text(desc, fontSize = 12.sp, color = Color(0xFF3F4948))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(size, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TriageGreen)
                                }
                            }
                        }
                    }
                }
            }

            // Hardware Acceleration (Snapdragon NPU / Hexagon)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TealPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = "NPU", tint = TealPrimary)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Snapdragon Hexagon NPU",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                            Text(
                                text = "Qualcomm QNN runtime acceleration (~14ms latency)",
                                fontSize = 12.sp,
                                color = Color(0xFF3F4948)
                            )
                        }

                        Switch(
                            checked = state.isHardwareNpuActive,
                            onCheckedChange = { viewModel.toggleNpu(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary, checkedTrackColor = TealPrimaryContainer),
                            modifier = Modifier.testTag("switch_npu_toggle")
                        )
                    }
                }
            }

            // Privacy & Local Compliance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = "Security", tint = TealPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Patient Privacy & Data Security",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "• All patient data is stored locally in an encrypted Room SQLite database (AES-256 GCM).\n• Zero cloud telemetry, zero remote API calls.\n• Data never leaves this device without explicit frontline worker initiation via iQOO Office Kit.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF3F4948),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { viewModel.resetDemoData {} },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_reset_demo_data"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (state.isResetting) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset / Reload Hackathon Demo Data", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // About App
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NIDAN AI (निदान AI)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TealPrimary
                        )
                        Text(
                            text = "v1.0 • Offline AI Frontline Clinical Triage",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Built for iQOO India Hackathon 2026",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                ClinicalSafetyNoticeBanner()
            }
        }
    }
}

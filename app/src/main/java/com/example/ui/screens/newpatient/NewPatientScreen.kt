package com.example.ui.screens.newpatient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.ScreeningType
import com.example.ui.components.OfflineStatusBar
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalOutlineVariant
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

@Composable
fun NewPatientScreen(
    viewModel: NewPatientViewModel,
    onNavigateBack: () -> Unit,
    onStartScreening: (patientId: String, type: ScreeningType) -> Unit,
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
                    onClick = {
                        if (state.currentStep == 2) viewModel.goToStep1() else onNavigateBack()
                    },
                    modifier = Modifier.testTag("new_patient_back_button")
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
                        text = if (state.currentStep == 1) "Step 1: Patient Information" else "Step 2: Choose Screening",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "ID: ${state.patientId} • Offline Encrypted",
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
                .testTag("new_patient_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OfflineStatusBar(isCompact = true)
            }

            if (state.currentStep == 1) {
                // Quick Demo Autofill Chips for Hackathon Judges
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚡ Quick Demo Prefill (Hackathon Cases)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = state.name == "Aarav Sharma",
                                onClick = {
                                    viewModel.prefillDemoPatient(
                                        "Aarav Sharma",
                                        7,
                                        "Male",
                                        "Kalyanpur Ward 4",
                                        "91-4829-1024-8831"
                                    )
                                },
                                label = { Text("Aarav (7y M)") },
                                modifier = Modifier.testTag("prefill_aarav")
                            )
                            FilterChip(
                                selected = state.name == "Meera Patil",
                                onClick = {
                                    viewModel.prefillDemoPatient(
                                        "Meera Patil",
                                        32,
                                        "Female",
                                        "Shivaji Nagar",
                                        "91-3091-8742-1940"
                                    )
                                },
                                label = { Text("Meera (32y F)") },
                                modifier = Modifier.testTag("prefill_meera")
                            )
                            FilterChip(
                                selected = state.name == "Rohan Kumar",
                                onClick = {
                                    viewModel.prefillDemoPatient(
                                        "Rohan Kumar",
                                        4,
                                        "Male",
                                        "Rampur Sector B",
                                        "91-1120-7452-9902"
                                    )
                                },
                                label = { Text("Rohan (4y M)") },
                                modifier = Modifier.testTag("prefill_rohan")
                            )
                        }
                    }
                }

                // Patient Name Input
                item {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Patient Name (मरीज का नाम) *") },
                        placeholder = { Text("e.g. Aarav Sharma") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("patient_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ClinicalSurface,
                            unfocusedContainerColor = ClinicalSurface
                        ),
                        singleLine = true
                    )
                }

                // Age and Gender Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.age,
                            onValueChange = { viewModel.updateAge(it) },
                            label = { Text("Age (उम्र) *") },
                            placeholder = { Text("e.g. 7") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("patient_age_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ClinicalSurface,
                                unfocusedContainerColor = ClinicalSurface
                            ),
                            singleLine = true
                        )

                        // Gender Selector
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "Gender (लिंग)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Female", "Male", "Other").forEach { g ->
                                    FilterChip(
                                        selected = state.gender == g,
                                        onClick = { viewModel.updateGender(g) },
                                        label = { Text(g, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = TealPrimaryContainer,
                                            selectedLabelColor = TealPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Village / Area
                item {
                    OutlinedTextField(
                        value = state.villageArea,
                        onValueChange = { viewModel.updateVillage(it) },
                        label = { Text("Village / Ward / Area (गाँव / क्षेत्र)") },
                        placeholder = { Text("e.g. Kalyanpur Ward 4") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("patient_village_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ClinicalSurface,
                            unfocusedContainerColor = ClinicalSurface
                        ),
                        singleLine = true
                    )
                }

                // Optional ABHA ID
                item {
                    OutlinedTextField(
                        value = state.abhaId,
                        onValueChange = { viewModel.updateAbhaId(it) },
                        label = { Text("ABHA ID (आभा संख्या - Optional)") },
                        placeholder = { Text("e.g. 91-4829-1024-8831") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("patient_abha_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ClinicalSurface,
                            unfocusedContainerColor = ClinicalSurface
                        ),
                        singleLine = true
                    )
                }

                if (state.errorMessage != null) {
                    item {
                        Text(
                            text = state.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Continue Button
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.proceedToStep2() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("patient_continue_step2_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Continue to Screening Selection", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Continue")
                    }
                }
            } else {
                // STEP 2: Choose Screening Cards
                item {
                    Text(
                        text = "Select Screening Method for ${state.name} (${state.age}y ${state.gender})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a specialized on-device clinical workflow:",
                        fontSize = 13.sp,
                        color = Color(0xFF3F4948)
                    )
                }

                val options = listOf(
                    Triple(ScreeningType.CHILD_MUAC, Icons.Default.ChildCare, "Assess nutritional risk using color band segmentation"),
                    Triple(ScreeningType.ANEMIA, Icons.Default.Visibility, "Check visible signs of conjunctival pallor"),
                    Triple(ScreeningType.VOICE_TRIAGE, Icons.Default.Mic, "Describe symptoms naturally in regional languages"),
                    Triple(ScreeningType.SKIN_SCREENING, Icons.Default.Healing, "Check visible dermatological lesion patterns"),
                    Triple(ScreeningType.GENERAL, Icons.Default.MedicalServices, "General comprehensive clinical triage workflow")
                )

                items(options.size) { index ->
                    val (type, icon, desc) = options[index]
                    val isSelected = state.selectedScreeningType == type

                    Card(
                        onClick = { viewModel.selectScreeningType(type) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TealPrimary else ClinicalOutlineVariant,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("select_screening_${type.name}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) TealPrimaryContainer.copy(alpha = 0.35f) else ClinicalSurface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectScreeningType(type) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TealPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = type.displayName, tint = TealPrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = type.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.saveAndStartScreening { patientId, type ->
                                onStartScreening(patientId, type)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_screening_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Start ${state.selectedScreeningType.displayName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Start")
                    }
                }
            }
        }
    }
}

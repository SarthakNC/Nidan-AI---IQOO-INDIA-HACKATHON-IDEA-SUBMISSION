package com.example.ui.screens.patients

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.domain.model.Patient
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.TriageBadge
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

@Composable
fun PatientsListScreen(
    viewModel: PatientsViewModel,
    onNavigateToNewPatient: () -> Unit,
    onNavigateToPatientProfile: (patientId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val patients by viewModel.patients.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewPatient,
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_patient")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Patient")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("patients_list_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Patient Directory",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1B)
                        )
                        Text(
                            text = "Encrypted Local Patient Records",
                            fontSize = 13.sp,
                            color = Color(0xFF3F4948)
                        )
                    }
                }
            }

            item {
                OfflineStatusBar(isCompact = true)
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search by name, ID, or village...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TealPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patients_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ClinicalSurface,
                        unfocusedContainerColor = ClinicalSurface
                    ),
                    singleLine = true
                )
            }

            if (patients.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No patient records match your query.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(patients) { patient ->
                    PatientItemCard(
                        patient = patient,
                        onClick = { onNavigateToPatientProfile(patient.patientId) },
                        modifier = Modifier.testTag("patient_card_${patient.patientId}")
                    )
                }
            }
        }
    }
}

@Composable
fun PatientItemCard(
    patient: Patient,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                    .clip(CircleShape)
                    .background(TealPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Patient Avatar",
                    tint = TealPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patient.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${patient.age}y • ${patient.gender.take(1)}",
                        fontSize = 13.sp,
                        color = Color(0xFF3F4948)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${patient.patientId} • ${patient.villageArea}",
                    fontSize = 12.sp,
                    color = Color(0xFF6F7977)
                )
            }

            TriageBadge(level = patient.latestTriageLevel)
        }
    }
}

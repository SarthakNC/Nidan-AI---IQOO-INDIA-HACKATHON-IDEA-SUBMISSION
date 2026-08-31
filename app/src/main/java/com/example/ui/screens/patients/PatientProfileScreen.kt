package com.example.ui.screens.patients

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.domain.model.Patient
import com.example.domain.model.ScreeningRecord
import com.example.security.SecureStorage
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.RecentScreeningItemCard
import com.example.ui.components.TriageBadge
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer

@Composable
fun PatientProfileScreen(
    patientId: String,
    viewModel: PatientsViewModel,
    onNavigateBack: () -> Unit,
    onStartAnemia: (patientId: String) -> Unit,
    onStartMuac: (patientId: String) -> Unit,
    onStartVoice: (patientId: String) -> Unit,
    onNavigateToHandover: () -> Unit,
    modifier: Modifier = Modifier
) {
    var patient by remember { mutableStateOf<Patient?>(null) }
    val screeningsFlow = remember(patientId) { viewModel.getScreeningsForPatient(patientId) }
    val screenings by screeningsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(patientId) {
        patient = viewModel.getPatient(patientId)
    }

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
                    modifier = Modifier.testTag("patient_profile_back_button")
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
                        text = patient?.name ?: "Patient Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "ID: $patientId • Offline EHR",
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
                .testTag("patient_profile_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OfflineStatusBar(isCompact = true)
            }

            // Patient Identity Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(TealPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Patient Avatar",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = patient?.name ?: "",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191C1B)
                                )
                                Text(
                                    text = "${patient?.age ?: ""} yrs • ${patient?.gender ?: ""} • ${patient?.villageArea ?: ""}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF3F4948)
                                )
                            }
                            patient?.let {
                                TriageBadge(level = it.latestTriageLevel)
                            }
                        }

                        if (!patient?.abhaId.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ABHA ID: ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3F4948)
                                    )
                                    Text(
                                        text = SecureStorage.maskAbhaId(patient?.abhaId ?: ""),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF191C1B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Screening Launcher for this Patient
            item {
                Text(
                    text = "Perform Screening for Patient",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onStartAnemia(patientId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Visibility, contentDescription = "Anemia", tint = TealPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Anemia", fontSize = 11.5.sp, color = Color(0xFF191C1B))
                        }
                    }

                    OutlinedButton(
                        onClick = { onStartMuac(patientId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChildCare, contentDescription = "MUAC", tint = TealPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("MUAC", fontSize = 11.5.sp, color = Color(0xFF191C1B))
                        }
                    }

                    OutlinedButton(
                        onClick = { onStartVoice(patientId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice", tint = TealPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Voice", fontSize = 11.5.sp, color = Color(0xFF191C1B))
                        }
                    }
                }
            }

            // Screening History Timeline
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Screening History (${screenings.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                }
            }

            if (screenings.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ClinicalSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No screening records for this patient yet.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(screenings) { record ->
                    RecentScreeningItemCard(
                        record = record,
                        onClick = { /* View details modal or card */ }
                    )
                }
            }

            // Doctor Handover Action
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateToHandover,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("patient_handover_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Handover to Doctor's Workstation", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                ClinicalSafetyNoticeBanner()
            }
        }
    }
}

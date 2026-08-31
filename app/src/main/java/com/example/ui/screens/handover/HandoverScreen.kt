package com.example.ui.screens.handover

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
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.officekit.ConnectionStatus
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import com.example.ui.theme.TriageGreen

@Composable
fun HandoverScreen(
    viewModel: HandoverViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.officeKitState.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("handover_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "PHC Doctor Handover",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "iQOO Office Kit Local Workstation Sync",
                        fontSize = 13.sp,
                        color = Color(0xFF3F4948)
                    )
                }
            }

            item {
                OfflineStatusBar(isCompact = true)
            }

            // Connection Diagram Card
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
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFLINE WORKSTATION BRIDGE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual Bridge Diagram: Phone -> Wi-Fi Direct -> PC
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Phone Node
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Smartphone, contentDescription = "Phone", tint = TealPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("iQOO Field Device", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            // Connection Line / Signal
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = "Sync Wave",
                                    tint = if (state.status == ConnectionStatus.CONNECTED_DOCTOR_PC || state.status == ConnectionStatus.TRANSFERRING) TealPrimary else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (state.status == ConnectionStatus.TRANSFERRING) "Transferring..." else "TLS 1.3 Local",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Workstation Node
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (state.status == ConnectionStatus.CONNECTED_DOCTOR_PC || state.status == ConnectionStatus.TRANSFER_COMPLETED) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Computer,
                                        contentDescription = "Doctor Workstation",
                                        tint = if (state.status == ConnectionStatus.CONNECTED_DOCTOR_PC || state.status == ConnectionStatus.TRANSFER_COMPLETED) TriageGreen else Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("PHC Doctor PC", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (state.status) {
                                                    ConnectionStatus.CONNECTED_DOCTOR_PC, ConnectionStatus.TRANSFER_COMPLETED -> TriageGreen
                                                    ConnectionStatus.SEARCHING_WORKSTATION, ConnectionStatus.TRANSFERRING -> Color(0xFFF59E0B)
                                                    else -> Color.Gray
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = state.workstationName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191C1B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = state.message,
                                    fontSize = 12.sp,
                                    color = Color(0xFF3F4948)
                                )
                            }
                        }
                    }
                }
            }

            // Sync Records Card
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pending Handover Records",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191C1B)
                                )
                                Text(
                                    text = "Unsynchronized triage records on device",
                                    fontSize = 12.sp,
                                    color = Color(0xFF3F4948)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TealPrimaryContainer)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$pendingCount records",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TealPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.startTransfer() },
                            enabled = state.status != ConnectionStatus.TRANSFERRING,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("btn_transfer_records"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            if (state.status == ConnectionStatus.TRANSFERRING) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Transferring to Workstation...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = "Transfer")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Transfer Records to PHC Workstation", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // iQOO Office Kit Screen Mirroring Mode
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
                            Icon(
                                imageVector = if (state.isMirroringActive) Icons.Default.CastConnected else Icons.Default.Cast,
                                contentDescription = "Screen Mirror",
                                tint = TealPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "iQOO Office Kit Screen Mirror",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1B)
                            )
                            Text(
                                text = "Display live screening view on Doctor's monitor",
                                fontSize = 12.sp,
                                color = Color(0xFF3F4948)
                            )
                        }

                        Switch(
                            checked = state.isMirroringActive,
                            onCheckedChange = { viewModel.toggleScreenMirror() },
                            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary, checkedTrackColor = TealPrimaryContainer),
                            modifier = Modifier.testTag("switch_screen_mirror")
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

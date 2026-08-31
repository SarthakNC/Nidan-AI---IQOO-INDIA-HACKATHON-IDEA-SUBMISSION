package com.example.ui.screens.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.ClinicalSafetyNoticeBanner
import com.example.ui.components.OfflineStatusBar
import com.example.ui.components.RecentScreeningItemCard
import com.example.ui.theme.ClinicalBackground
import com.example.ui.theme.ClinicalSurface
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryContainer
import com.example.ui.theme.TriageGreen
import com.example.ui.theme.TriageRed
import com.example.ui.theme.TriageYellow

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToPatientProfile: (patientId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenings by viewModel.screenings.collectAsStateWithLifecycle()
    val currentFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(ClinicalBackground)
                .padding(innerPadding)
                .testTag("history_screen_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Screening History Timeline",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1B)
                    )
                    Text(
                        text = "Field Triage Logs & Clinical Outcomes",
                        fontSize = 13.sp,
                        color = Color(0xFF3F4948)
                    )
                }
            }

            item {
                OfflineStatusBar(isCompact = true)
            }

            // Triage Filters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = currentFilter == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("All Logs", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimaryContainer,
                            selectedLabelColor = TealPrimary
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )
                    FilterChip(
                        selected = currentFilter == TriageLevel.RED,
                        onClick = { viewModel.setFilter(TriageLevel.RED) },
                        label = { Text("RED Risk", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TriageRed.copy(alpha = 0.2f),
                            selectedLabelColor = TriageRed
                        ),
                        modifier = Modifier.testTag("filter_red")
                    )
                    FilterChip(
                        selected = currentFilter == TriageLevel.YELLOW,
                        onClick = { viewModel.setFilter(TriageLevel.YELLOW) },
                        label = { Text("YELLOW", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TriageYellow.copy(alpha = 0.2f),
                            selectedLabelColor = TriageYellow
                        ),
                        modifier = Modifier.testTag("filter_yellow")
                    )
                    FilterChip(
                        selected = currentFilter == TriageLevel.GREEN,
                        onClick = { viewModel.setFilter(TriageLevel.GREEN) },
                        label = { Text("GREEN", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TriageGreen.copy(alpha = 0.2f),
                            selectedLabelColor = TriageGreen
                        ),
                        modifier = Modifier.testTag("filter_green")
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
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No screening records found for this filter.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(screenings) { record ->
                    RecentScreeningItemCard(
                        record = record,
                        onClick = { onNavigateToPatientProfile(record.patientId) },
                        modifier = Modifier.testTag("history_item_${record.id}")
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                ClinicalSafetyNoticeBanner()
            }
        }
    }
}

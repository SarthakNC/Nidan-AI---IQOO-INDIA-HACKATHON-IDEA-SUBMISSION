package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val hindiTitle: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Home", "होम", Icons.Default.Home)
    object Patients : BottomNavItem(Screen.Patients.route, "Patients", "मरीज", Icons.Default.People)
    object History : BottomNavItem(Screen.History.route, "History", "इतिहास", Icons.Default.History)
    object Handover : BottomNavItem(Screen.Handover.route, "PHC Sync", "हैंडओवर", Icons.Default.Sync)
    object Settings : BottomNavItem(Screen.Settings.route, "Settings", "सेटिंग्स", Icons.Default.Settings)
}

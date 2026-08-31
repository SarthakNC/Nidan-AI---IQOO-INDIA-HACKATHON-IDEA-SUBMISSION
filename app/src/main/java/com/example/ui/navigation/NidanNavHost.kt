package com.example.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.domain.model.ScreeningType
import com.example.ui.screens.anemia.AnemiaScanScreen
import com.example.ui.screens.anemia.AnemiaScanViewModel
import com.example.ui.screens.handover.HandoverScreen
import com.example.ui.screens.handover.HandoverViewModel
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.history.HistoryViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.muac.MuacScanScreen
import com.example.ui.screens.muac.MuacScanViewModel
import com.example.ui.screens.newpatient.NewPatientScreen
import com.example.ui.screens.newpatient.NewPatientViewModel
import com.example.ui.screens.patients.PatientProfileScreen
import com.example.ui.screens.patients.PatientsListScreen
import com.example.ui.screens.patients.PatientsViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel
import com.example.ui.screens.voice.VoiceTriageScreen
import com.example.ui.screens.voice.VoiceTriageViewModel

@Composable
fun NidanNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.fillMaxSize()
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            val homeVm: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeVm,
                onNavigateToNewPatient = { navController.navigate(Screen.NewPatient.route) },
                onNavigateToAnemia = { patientId -> navController.navigate(Screen.AnemiaScan.createRoute(patientId)) },
                onNavigateToMuac = { patientId -> navController.navigate(Screen.MuacScan.createRoute(patientId)) },
                onNavigateToVoice = { patientId -> navController.navigate(Screen.VoiceTriage.createRoute(patientId)) },
                onNavigateToHandover = { navController.navigate(Screen.Handover.route) },
                onNavigateToPatientProfile = { patientId -> navController.navigate(Screen.PatientProfile.createRoute(patientId)) }
            )
        }

        // Patients Directory
        composable(Screen.Patients.route) {
            val patientsVm: PatientsViewModel = viewModel()
            PatientsListScreen(
                viewModel = patientsVm,
                onNavigateToNewPatient = { navController.navigate(Screen.NewPatient.route) },
                onNavigateToPatientProfile = { patientId -> navController.navigate(Screen.PatientProfile.createRoute(patientId)) }
            )
        }

        // Patient Profile Screen
        composable(
            route = Screen.PatientProfile.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: "P-1024"
            val patientsVm: PatientsViewModel = viewModel()
            PatientProfileScreen(
                patientId = patientId,
                viewModel = patientsVm,
                onNavigateBack = { navController.popBackStack() },
                onStartAnemia = { pid -> navController.navigate(Screen.AnemiaScan.createRoute(pid)) },
                onStartMuac = { pid -> navController.navigate(Screen.MuacScan.createRoute(pid)) },
                onStartVoice = { pid -> navController.navigate(Screen.VoiceTriage.createRoute(pid)) },
                onNavigateToHandover = { navController.navigate(Screen.Handover.route) }
            )
        }

        // Screening History Timeline
        composable(Screen.History.route) {
            val historyVm: HistoryViewModel = viewModel()
            HistoryScreen(
                viewModel = historyVm,
                onNavigateToPatientProfile = { patientId -> navController.navigate(Screen.PatientProfile.createRoute(patientId)) }
            )
        }

        // PHC Doctor Handover
        composable(Screen.Handover.route) {
            val handoverVm: HandoverViewModel = viewModel()
            HandoverScreen(viewModel = handoverVm)
        }

        // Settings
        composable(Screen.Settings.route) {
            val settingsVm: SettingsViewModel = viewModel()
            SettingsScreen(viewModel = settingsVm)
        }

        // New Patient Flow
        composable(Screen.NewPatient.route) {
            val newPatientVm: NewPatientViewModel = viewModel()
            NewPatientScreen(
                viewModel = newPatientVm,
                onNavigateBack = { navController.popBackStack() },
                onStartScreening = { patientId, type ->
                    when (type) {
                        ScreeningType.ANEMIA -> navController.navigate(Screen.AnemiaScan.createRoute(patientId))
                        ScreeningType.CHILD_MUAC -> navController.navigate(Screen.MuacScan.createRoute(patientId))
                        ScreeningType.VOICE_TRIAGE -> navController.navigate(Screen.VoiceTriage.createRoute(patientId))
                        else -> navController.navigate(Screen.VoiceTriage.createRoute(patientId))
                    }
                }
            )
        }

        // Anemia Scan Screen
        composable(
            route = Screen.AnemiaScan.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: "P-1024"
            val anemiaVm = AnemiaScanViewModel(app, patientId)
            AnemiaScanScreen(
                viewModel = anemiaVm,
                onNavigateBack = { navController.popBackStack() },
                onScreeningSaved = { navController.navigate(Screen.Home.route) }
            )
        }

        // MUAC Child Nutrition Scan Screen
        composable(
            route = Screen.MuacScan.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: "P-1026"
            val muacVm = MuacScanViewModel(app, patientId)
            MuacScanScreen(
                viewModel = muacVm,
                onNavigateBack = { navController.popBackStack() },
                onScreeningSaved = { navController.navigate(Screen.Home.route) }
            )
        }

        // Voice Triage Screen
        composable(
            route = Screen.VoiceTriage.route,
            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: "P-1025"
            val voiceVm = VoiceTriageViewModel(app, patientId)
            VoiceTriageScreen(
                viewModel = voiceVm,
                onNavigateBack = { navController.popBackStack() },
                onScreeningSaved = { navController.navigate(Screen.Home.route) }
            )
        }
    }
}

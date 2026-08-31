package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Patients : Screen("patients")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Handover : Screen("handover")

    object NewPatient : Screen("new_patient")
    object AnemiaScan : Screen("anemia_scan/{patientId}") {
        fun createRoute(patientId: String) = "anemia_scan/$patientId"
    }
    object MuacScan : Screen("muac_scan/{patientId}") {
        fun createRoute(patientId: String) = "muac_scan/$patientId"
    }
    object VoiceTriage : Screen("voice_triage/{patientId}") {
        fun createRoute(patientId: String) = "voice_triage/$patientId"
    }
    object Result : Screen("result/{patientId}/{screeningType}") {
        fun createRoute(patientId: String, screeningType: String) = "result/$patientId/$screeningType"
    }
    object PatientProfile : Screen("patient_profile/{patientId}") {
        fun createRoute(patientId: String) = "patient_profile/$patientId"
    }
}

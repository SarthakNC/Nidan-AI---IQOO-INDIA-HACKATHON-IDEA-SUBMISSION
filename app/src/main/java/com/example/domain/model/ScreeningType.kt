package com.example.domain.model

enum class ScreeningType(val displayName: String, val hindiName: String, val shortDescription: String) {
    ANEMIA("Anemia Scan", "एनीमिया जांच", "Check visible signs of anemia risk"),
    CHILD_MUAC("Child Nutrition (MUAC)", "बाल पोषण जांच", "Assess nutritional risk using MUAC"),
    VOICE_TRIAGE("Voice Triage", "आवाज से जांच", "Describe symptoms naturally"),
    SKIN_SCREENING("Skin Screening", "त्वचा जांच", "Check visible dermatological patterns"),
    GENERAL("General Screening", "सामान्य जांच", "Comprehensive frontline clinical triage")
}

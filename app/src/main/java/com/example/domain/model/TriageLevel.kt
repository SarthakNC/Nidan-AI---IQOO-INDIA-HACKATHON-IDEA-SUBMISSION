package com.example.domain.model

enum class TriageLevel(val label: String, val hindiLabel: String, val defaultAction: String) {
    GREEN("Low Risk", "कम जोखिम", "Routine follow-up / home care guidance."),
    YELLOW("Moderate Risk", "मध्यम जोखिम", "Primary Health Centre assessment recommended."),
    RED("High Risk", "उच्च जोखिम (गंभीर)", "Immediate medical evaluation recommended.")
}

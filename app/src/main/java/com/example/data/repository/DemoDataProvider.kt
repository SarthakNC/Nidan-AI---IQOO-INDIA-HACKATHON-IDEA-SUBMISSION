package com.example.data.repository

import com.example.data.local.PatientEntity
import com.example.data.local.ScreeningEntity
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel

object DemoDataProvider {

    fun getInitialPatients(): List<PatientEntity> {
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        val oneDay = 86400_000L

        return listOf(
            PatientEntity(
                patientId = "P-1024",
                name = "Aarav Sharma",
                age = 7,
                gender = "Male",
                villageArea = "Kalyanpur Ward 4",
                abhaId = "91-4829-1024-8831",
                latestTriageLevel = TriageLevel.YELLOW.name,
                totalScreenings = 2,
                createdAt = now - (2 * oneHour)
            ),
            PatientEntity(
                patientId = "P-1025",
                name = "Meera Patil",
                age = 32,
                gender = "Female",
                villageArea = "Shivaji Nagar",
                abhaId = "91-3091-8742-1940",
                latestTriageLevel = TriageLevel.GREEN.name,
                totalScreenings = 1,
                createdAt = now - (4 * oneHour)
            ),
            PatientEntity(
                patientId = "P-1026",
                name = "Rohan Kumar",
                age = 4,
                gender = "Male",
                villageArea = "Rampur Sector B",
                abhaId = "91-1120-7452-9902",
                latestTriageLevel = TriageLevel.RED.name,
                totalScreenings = 3,
                createdAt = now - (6 * oneHour)
            ),
            PatientEntity(
                patientId = "P-1027",
                name = "Sunita Devi",
                age = 28,
                gender = "Female",
                villageArea = "Kalyanpur Ward 2",
                abhaId = "91-6401-2299-4412",
                latestTriageLevel = TriageLevel.YELLOW.name,
                totalScreenings = 1,
                createdAt = now - oneDay
            ),
            PatientEntity(
                patientId = "P-1028",
                name = "Vikram Rathore",
                age = 45,
                gender = "Male",
                villageArea = "Gram Panchayat North",
                abhaId = "91-8890-3341-7650",
                latestTriageLevel = TriageLevel.GREEN.name,
                totalScreenings = 1,
                createdAt = now - (2 * oneDay)
            )
        )
    }

    fun getInitialScreenings(): List<ScreeningEntity> {
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L

        return listOf(
            ScreeningEntity(
                patientId = "P-1024",
                patientName = "Aarav Sharma",
                patientAge = 7,
                patientGender = "Male",
                screeningType = ScreeningType.CHILD_MUAC.name,
                triageLevel = TriageLevel.YELLOW.name,
                triageTitle = "Moderate Nutritional Risk (MAM Pattern)",
                recommendedAction = "Refer to Primary Health Centre for supplementary nutrition counselling and growth monitoring.",
                clinicalRationale = "MUAC measurement (121 mm) falls in the yellow alert band. Growth monitoring recommended.",
                confidence = 88,
                findingsJson = "{\"muacMm\":121, \"zone\":\"YELLOW\"}",
                rawNotes = "Screened with standard MUAC arm tape. Child active but underweight.",
                timestamp = now - (2 * oneHour),
                isSyncedWithPhc = false
            ),
            ScreeningEntity(
                patientId = "P-1025",
                patientName = "Meera Patil",
                patientAge = 32,
                patientGender = "Female",
                screeningType = ScreeningType.ANEMIA.name,
                triageLevel = TriageLevel.GREEN.name,
                triageTitle = "Low Visible Anemia Risk",
                recommendedAction = "Routine follow-up. Counsel on iron-rich dietary intake and standard IFA prophylaxis.",
                clinicalRationale = "Normal conjunctival redness index detected. No pallor markers found.",
                confidence = 92,
                findingsJson = "{\"pallorDetected\":false}",
                rawNotes = "Routine antenatal field check. Conjunctiva well-vascularized.",
                timestamp = now - (4 * oneHour),
                isSyncedWithPhc = false
            ),
            ScreeningEntity(
                patientId = "P-1026",
                patientName = "Rohan Kumar",
                patientAge = 4,
                patientGender = "Male",
                screeningType = ScreeningType.VOICE_TRIAGE.name,
                triageLevel = TriageLevel.RED.name,
                triageTitle = "High Risk - Urgent Medical Attention",
                recommendedAction = "Immediate transport/referral to nearest Community Health Centre / District Hospital.",
                clinicalRationale = "High fever for 4 days with chest indrawing and rapid breathing.",
                confidence = 94,
                findingsJson = "{\"symptoms\":[\"High Fever\", \"Breathing Difficulty\", \"Severe Lethargy\"], \"durationDays\":4}",
                rawNotes = "Audio captured: Patient breathing rapidly with persistent fever.",
                timestamp = now - (6 * oneHour),
                isSyncedWithPhc = false
            ),
            ScreeningEntity(
                patientId = "P-1027",
                patientName = "Sunita Devi",
                patientAge = 28,
                patientGender = "Female",
                screeningType = ScreeningType.ANEMIA.name,
                triageLevel = TriageLevel.YELLOW.name,
                triageTitle = "Anemia Risk Pattern Detected",
                recommendedAction = "Refer to Primary Health Centre for lab hemoglobin estimation (Hb) and IFA supplementation review.",
                clinicalRationale = "Marked palpebral conjunctiva pallor detected in inner eyelid scan.",
                confidence = 85,
                findingsJson = "{\"pallorDetected\":true}",
                rawNotes = "Complained of general fatigue and dizziness.",
                timestamp = now - (20 * oneHour),
                isSyncedWithPhc = true
            )
        )
    }
}

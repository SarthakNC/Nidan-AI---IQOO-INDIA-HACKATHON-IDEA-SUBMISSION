package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.ScreeningFinding
import com.example.domain.model.ScreeningType
import com.example.domain.model.TriageLevel
import com.example.domain.triage.DeterministicTriageEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private val triageEngine = DeterministicTriageEngine()

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Nidan AI", appName)
    }

    @Test
    fun `test MUAC severe acute malnutrition triggers RED triage`() {
        val finding = ScreeningFinding(
            screeningType = ScreeningType.CHILD_MUAC,
            muacMeasurementMm = 110,
            muacColorZone = "RED"
        )
        val result = triageEngine.evaluate(finding)
        assertEquals(TriageLevel.RED, result.level)
        assertTrue(result.title.contains("High Nutritional Risk"))
    }

    @Test
    fun `test MUAC moderate acute malnutrition triggers YELLOW triage`() {
        val finding = ScreeningFinding(
            screeningType = ScreeningType.CHILD_MUAC,
            muacMeasurementMm = 120,
            muacColorZone = "YELLOW"
        )
        val result = triageEngine.evaluate(finding)
        assertEquals(TriageLevel.YELLOW, result.level)
    }

    @Test
    fun `test MUAC normal measurement triggers GREEN triage`() {
        val finding = ScreeningFinding(
            screeningType = ScreeningType.CHILD_MUAC,
            muacMeasurementMm = 135,
            muacColorZone = "GREEN"
        )
        val result = triageEngine.evaluate(finding)
        assertEquals(TriageLevel.GREEN, result.level)
    }

    @Test
    fun `test voice danger signs trigger RED triage`() {
        val finding = ScreeningFinding(
            screeningType = ScreeningType.VOICE_TRIAGE,
            symptoms = listOf("High Fever", "Chest Indrawing"),
            durationDays = 4,
            respiratoryDistress = true
        )
        val result = triageEngine.evaluate(finding)
        assertEquals(TriageLevel.RED, result.level)
    }
}

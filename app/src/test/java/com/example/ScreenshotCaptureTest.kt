package com.example

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.NidanDatabase
import com.example.data.repository.DemoDataProvider
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
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ScreenshotCaptureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: NidanDatabase

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        db = NidanDatabase.getInstance(app)

        runBlocking {
            if (db.patientDao().getPatientCount() == 0) {
                db.patientDao().insertPatients(DemoDataProvider.getInitialPatients())
                db.screeningDao().insertScreenings(DemoDataProvider.getInitialScreenings())
            }
        }
    }

    @Test
    fun capture_01_home_dashboard() {
        val vm = HomeViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        viewModel = vm,
                        onNavigateToNewPatient = {},
                        onNavigateToAnemia = {},
                        onNavigateToMuac = {},
                        onNavigateToVoice = {},
                        onNavigateToHandover = {},
                        onNavigateToPatientProfile = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/01-home.png")
    }

    @Test
    fun capture_02_new_patient_intake() {
        val vm = NewPatientViewModel(app)
        vm.updateName("Sunita Devi")
        vm.updateAge("24")
        vm.updateGender("Female")
        vm.updateVillage("Rampur PHC Sector 3")
        vm.updateAbhaId("91-4521-8890-1234")

        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NewPatientScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onStartScreening = { _, _ -> }
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/02-new-patient.png")
    }

    @Test
    fun capture_03_screening_selection() {
        val vm = NewPatientViewModel(app)
        vm.updateName("Sunita Devi")
        vm.updateAge("24")
        vm.updateGender("Female")
        vm.updateVillage("Rampur Sector 3")
        vm.proceedToStep2()

        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NewPatientScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onStartScreening = { _, _ -> }
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/03-screening-selection.png")
    }

    @Test
    fun capture_04_anemia_scan() {
        val vm = AnemiaScanViewModel(app, "P-1024")
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnemiaScanScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/04-anemia-scan.png")
    }

    @Test
    fun capture_05_anemia_result() {
        val vm = AnemiaScanViewModel(app, "P-1024")
        vm.toggleSimulatedPallor(true)
        vm.captureAndAnalyze()

        // Allow coroutine delay to finish
        Thread.sleep(600)

        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnemiaScanScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/05-anemia-result.png")
    }

    @Test
    fun capture_06_muac_screening() {
        val vm = MuacScanViewModel(app, "P-1026")
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MuacScanScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/06-muac-screening.png")
    }

    @Test
    fun capture_07_muac_result() {
        val vm = MuacScanViewModel(app, "P-1026")
        vm.selectSimulatedZone("RED")
        vm.captureAndAnalyze()

        Thread.sleep(600)

        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MuacScanScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/07-muac-result.png")
    }

    @Test
    fun capture_08_voice_triage() {
        val vm = VoiceTriageViewModel(app, "P-1025")
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VoiceTriageScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/08-voice-triage.png")
    }

    @Test
    fun capture_09_voice_triage_result() {
        val vm = VoiceTriageViewModel(app, "P-1025")
        vm.startListeningWithPreset("Fever 4 days, chest indrawing, severe breathing difficulty and lethargy")

        Thread.sleep(1700)

        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VoiceTriageScreen(
                        viewModel = vm,
                        onNavigateBack = {},
                        onScreeningSaved = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/09-voice-result.png")
    }

    @Test
    fun capture_10_patients_directory() {
        val vm = PatientsViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PatientsListScreen(
                        viewModel = vm,
                        onNavigateToNewPatient = {},
                        onNavigateToPatientProfile = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/10-patients.png")
    }

    @Test
    fun capture_11_patient_profile() {
        val vm = PatientsViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PatientProfileScreen(
                        patientId = "P-1024",
                        viewModel = vm,
                        onNavigateBack = {},
                        onStartAnemia = {},
                        onStartMuac = {},
                        onStartVoice = {},
                        onNavigateToHandover = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/11-patient-profile.png")
    }

    @Test
    fun capture_12_screening_history() {
        val vm = HistoryViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HistoryScreen(
                        viewModel = vm,
                        onNavigateToPatientProfile = {}
                    )
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/12-history.png")
    }

    @Test
    fun capture_13_phc_handover() {
        val vm = HandoverViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HandoverScreen(viewModel = vm)
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/13-phc-handover.png")
    }

    @Test
    fun capture_14_settings() {
        val vm = SettingsViewModel(app)
        composeTestRule.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(viewModel = vm)
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "docs/screenshots/14-settings.png")
    }
}

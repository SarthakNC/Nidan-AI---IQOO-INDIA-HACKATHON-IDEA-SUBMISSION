package com.example.ai.speech

import kotlinx.coroutines.delay

interface SpeechRecognitionEngine {
    suspend fun transcribeAudio(audioBytes: ByteArray? = null, languageCode: String = "hi-IN"): SpeechTranscriptResult
}

data class SpeechTranscriptResult(
    val transcript: String,
    val languageDetected: String,
    val confidence: Float,
    val durationSeconds: Float,
    val isOfflineProcessed: Boolean = true
)

/**
 * Local Speech Recognition Engine abstraction.
 * Future integration target: Whisper-tiny INT8 running on Snapdragon Hexagon NPU via ONNX Runtime Mobile.
 */
class MockSpeechRecognitionEngine : SpeechRecognitionEngine {

    override suspend fun transcribeAudio(audioBytes: ByteArray?, languageCode: String): SpeechTranscriptResult {
        // Simulate local on-device ASR transcription latency
        delay(600)

        return when (languageCode) {
            "hi-IN" -> SpeechTranscriptResult(
                transcript = "मरीज को तीन दिनों से तेज बुखार है और जोड़ों में तेज दर्द है, भूख भी नहीं लग रही है।",
                languageDetected = "Hindi (hi-IN)",
                confidence = 0.93f,
                durationSeconds = 4.2f
            )
            "mr-IN" -> SpeechTranscriptResult(
                transcript = "रुग्णाला ३ दिवसांपासून ताप आहे आणि अशक्तपणा जाणवत आहे.",
                languageDetected = "Marathi (mr-IN)",
                confidence = 0.91f,
                durationSeconds = 3.8f
            )
            else -> SpeechTranscriptResult(
                transcript = "Patient has had high fever for three days, severe joint pain, and reduced appetite.",
                languageDetected = "English / Mixed (en-IN)",
                confidence = 0.95f,
                durationSeconds = 4.0f
            )
        }
    }
}

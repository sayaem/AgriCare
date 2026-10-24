package com.example.data.ai

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<Content>
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inline_data") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mime_type") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null,
    @Json(name = "error") val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: CandidateContent? = null
)

@JsonClass(generateAdapter = true)
data class CandidateContent(
    @Json(name = "parts") val parts: List<CandidatePart>? = null
)

@JsonClass(generateAdapter = true)
data class CandidatePart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: String? = null
)

// Structured agri-diagnosis result contract matching PRD Appendix B
@JsonClass(generateAdapter = true)
data class AgriDiagnosisResult(
    @Json(name = "crop_guess") val cropGuess: String? = "unknown",
    @Json(name = "issue_title") val issueTitle: String,
    @Json(name = "assessment_level") val assessmentLevel: String, // "likely", "uncertain", "insufficient_image"
    @Json(name = "visible_symptoms") val visibleSymptoms: List<String> = emptyList(),
    @Json(name = "alternative_causes") val alternativeCauses: List<String> = emptyList(),
    @Json(name = "immediate_steps") val immediateSteps: List<TreatmentStepDto> = emptyList(),
    @Json(name = "prevention_steps") val preventionSteps: List<String> = emptyList(),
    @Json(name = "follow_up_questions") val followUpQuestions: List<String> = emptyList(),
    @Json(name = "escalation_advice") val escalationAdvice: String? = null,
    @Json(name = "safety_notes") val safetyNotes: List<String> = emptyList(),
    @Json(name = "limitations") val limitations: String? = null,
    @Json(name = "language") val language: String = "en"
)

@JsonClass(generateAdapter = true)
data class TreatmentStepDto(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "priority") val priority: String // "now", "soon", "monitor"
)

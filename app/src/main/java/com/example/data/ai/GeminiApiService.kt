package com.example.data.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiRetrofitService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

class GeminiRepository(private val context: Context) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service = retrofit.create(GeminiRetrofitService::class.java)

    suspend fun analyzePlantImage(
        bitmap: Bitmap,
        languageCode: String, // "en" or "bn-BD"
        userNotes: String? = null
    ): Result<AgriDiagnosisResult> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return Result.failure(Exception("Gemini API key is not configured. Please add your GEMINI_API_KEY in the Secrets panel in AI Studio."))
        }

        try {
            // Compress and encode bitmap to base64 JPEG
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.NO_WRAP)

            val prompt = buildPrompt(languageCode, userNotes)

            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt),
                            Part(
                                inlineData = InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            )
                        )
                    )
                )
            )

            val response = service.generateContent(apiKey, request)

            if (response.error != null) {
                val errCode = response.error.code ?: 500
                val errMsg = response.error.message ?: "Unknown API error"
                if (errCode == 429 || errMsg.contains("quota", ignoreCase = true)) {
                    return Result.failure(Exception("API quota exceeded. Please try again later or consult offline guides."))
                }
                return Result.failure(Exception("Gemini API error ($errCode): $errMsg"))
            }

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Received empty response from Gemini AI."))

            // Parse JSON from response text (handling potential markdown code blocks)
            val cleanJson = extractJson(rawText)
            val adapter = moshi.adapter(AgriDiagnosisResult::class.java)
            val parsedResult = adapter.fromJson(cleanJson)

            if (parsedResult == null) {
                return Result.failure(Exception("Failed to parse AgriCare structured diagnosis result."))
            }

            return Result.success(parsedResult)

        } catch (e: retrofit2.HttpException) {
            val code = e.code()
            if (code == 429) {
                return Result.failure(Exception("API rate limit or quota exceeded. Please retry later."))
            }
            return Result.failure(Exception("Network error ($code): ${e.message()}"))
        } catch (e: java.net.UnknownHostException) {
            return Result.failure(Exception("No internet connection. AgriCare offline guides and saved history remain fully accessible."))
        } catch (e: java.net.SocketTimeoutException) {
            return Result.failure(Exception("Connection timed out. Please check your network and retry."))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun buildPrompt(languageCode: String, userNotes: String?): String {
        val notesClause = if (!userNotes.isNullOrBlank()) " Additional user context/notes: $userNotes." else ""
        return """
You are AgriCare, a cautious, professional plant-health assistant for farmers. Analyze the attached plant/crop image carefully.${notesClause}
Return ONLY valid JSON matching this exact schema (no markdown formatting outside the JSON object, or wrap in standard markdown json block):
{
  "crop_guess": "string or unknown",
  "issue_title": "string (name of likely disease or pest)",
  "assessment_level": "likely | uncertain | insufficient_image",
  "visible_symptoms": ["string"],
  "alternative_causes": ["string"],
  "immediate_steps": [{"title": "string", "description": "string", "priority": "now | soon | monitor"}],
  "prevention_steps": ["string"],
  "follow_up_questions": ["string"],
  "escalation_advice": "string (when to consult local agricultural officer)",
  "safety_notes": ["string"],
  "limitations": "string",
  "language": "$languageCode"
}
Ensure medical/agronomic cautiousness, advise local agricultural extension officers when uncertain, and provide results in language code $languageCode.
        """.trimIndent()
    }

    private fun extractJson(rawText: String): String {
        var text = rawText.trim()
        if (text.startsWith("```json")) {
            text = text.removePrefix("```json").trim()
        } else if (text.startsWith("```")) {
            text = text.removePrefix("```").trim()
        }
        if (text.endsWith("```")) {
            text = text.removeSuffix("```").trim()
        }
        return text
    }
}

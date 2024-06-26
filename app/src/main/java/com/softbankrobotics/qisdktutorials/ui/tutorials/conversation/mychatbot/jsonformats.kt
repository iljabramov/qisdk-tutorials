package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.mychatbot
import android.util.Log
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString as decode
import kotlinx.serialization.json.Json

private const val TAG = "QiChatbotActivity"

class JSONToAPI {
    companion object {
        fun getResponseMessage(body: String): String {
            try {
                val responseBody = Json.decode<JSONResponseBody>(body)
                return responseBody.choices.first().message.content
            } catch (e: Exception) {
                Log.e(TAG, "Error during serialization: ${e.message}")
                throw e
            }
        }

        fun getRequestBody(messages: List<JSONMessage>): String {
            try {
                val request = JSONRequestBody("gpt-3.5-turbo", messages)
                return Json.encodeToString(request)
            } catch (e: Exception) {
                Log.e(TAG, "Error during serialization: ${e.message}")
                throw e
            }
        }

    }
}


@Serializable
data class JSONMessageContent(
    val role: String,
    val content: String
)

@Serializable
data class JSONChoice(
    val index: Int,
    val message: JSONMessageContent,
    @Contextual
    val logprobs: Any? = null,
    val finish_reason: String
)

@Serializable
data class JSONUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class JSONResponseBody(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val system_fingerprint: String? = null,
    val choices: List<JSONChoice>,
    val usage: JSONUsage
)

@Serializable
data class JSONMessage(
    val role: String,
    val content: String
)

@Serializable
data class JSONRequestBody(
    val model: String,
    val messages: List<JSONMessage>
)
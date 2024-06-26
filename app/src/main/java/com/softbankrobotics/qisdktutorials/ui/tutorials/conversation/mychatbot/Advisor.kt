package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.mychatbot

import com.softbankrobotics.qisdktutorials.BuildConfig
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "QiChatbotActivity"

class Advisor {
    fun answerQuestion(messages: List<JSONMessage>): String {
        val apiKey = BuildConfig.ChatGPT_API_KEY
        val endpoint = "https://api.openai.com/v1/chat/completions"
        val client = OkHttpClient()
        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        // Construct the request body with past messages and the current question
        val requestBody = JSONToAPI.getRequestBody(messages).toRequestBody(jsonMediaType)
        Log.e(TAG, "Request Body $requestBody")

        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.e(TAG, "Unexpected code $response")
            }
            Log.e(TAG, "Response Code: ${response.code}")
            val answer = response.body?.string()
            Log.e(TAG, "Response: $answer")
            return if(answer != null) JSONToAPI.getResponseMessage(answer) else "Error: No Answer was given"
        }
    }
}

/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.mychatbot

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.conversation.BaseChatbot
import com.aldebaran.qi.sdk.`object`.conversation.BaseChatbotReaction
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.ReplyPriority
import com.aldebaran.qi.sdk.`object`.conversation.SpeechEngine
import com.aldebaran.qi.sdk.`object`.conversation.StandardReplyReaction
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.softbankrobotics.qisdktutorials.databinding.ConversationLayoutBinding
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationBinder
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationItemType
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity
import com.softbankrobotics.qisdktutorials.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException


private const val TAG = "QiChatbotActivity"

/**
 * The activity for the QiChatbot tutorial.
 */
class MyChatbotTutorialActivity : TutorialActivity<ConversationLayoutBinding>(), RobotLifecycleCallbacks {
    override fun inflateBinding(): ConversationLayoutBinding {
        return ConversationLayoutBinding.inflate(layoutInflater)
    }
    private var conversationBinder: ConversationBinder? = null

    // Store the Chat action.
    private lateinit var chat: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        // Bind the conversational events to the view.
        val conversationStatus = qiContext.conversation.status(qiContext.robotContext)
        conversationBinder = binding.conversationView.bindConversationTo(conversationStatus)

        // Create a new QiChatbot.
        val myChatbot = PepperChatbot(qiContext)

        // Create a new Chat action.
        chat = ChatBuilder.with(qiContext)
            .withChatbot(myChatbot)
            .withLocale(Constants.Locals.ENGLISH_LOCALE)
            .build()


        // Add an on started listener to the Chat action.
        chat.addOnStartedListener {
            val message = "Discussion started."
            Log.i(TAG, message)
            displayLine(message, ConversationItemType.INFO_LOG)
        }


        // Run the Chat action asynchronously.
        val chatFuture = chat.async().run()

        // Add a lambda to the action execution.
        chatFuture.thenConsume {
            if (it.hasError()) {
                val message = "Discussion finished with error."
                Log.e(TAG, message, it.error)
                displayLine(message, ConversationItemType.ERROR_LOG)
                val sayError = SayBuilder.with(qiContext)
                    .withText(message)
                    .withLocale(Constants.Locals.ENGLISH_LOCALE)
                    .build()

                sayError.run()
            }
        }
    }

    override fun onRobotFocusLost() {
        conversationBinder?.unbind()

        // Remove the listeners from the Chat action.
        chat.removeAllOnStartedListeners()
    }

    override fun onRobotFocusRefused(reason: String) {
        // Nothing here.
    }

    private fun displayLine(text: String, type: ConversationItemType) {
        runOnUiThread { binding.conversationView.addLine(text, type) }
    }
}

internal class PepperChatbot(context: QiContext?) : BaseChatbot(context) {

    override fun replyTo(phrase: Phrase, locale: Locale): StandardReplyReaction {
        Log.e(TAG, phrase.text)
        val ans = gpt.answerQuestion(phrase.text)
        Log.e(TAG, ans)
        return if (phrase.text !== "") {
            StandardReplyReaction(
                MyChatbotReaction(qiContext, ans),
                ReplyPriority.NORMAL
            )
        } else {
            StandardReplyReaction(
                MyChatbotReaction(qiContext, "I can just greet you"),
                ReplyPriority.FALLBACK
            )
        }
    }


    internal inner class MyChatbotReaction(context: QiContext?, private val answer: String) :
        BaseChatbotReaction(context) {
        private var fSay: Future<Void>? = null

        override fun runWith(speechEngine: SpeechEngine) {
            val say = SayBuilder.with(speechEngine)
                .withText(answer)
                .withLocale(Constants.Locals.ENGLISH_LOCALE)
                .build()
            fSay = say.async().run()

            try {
                fSay!!.get() // Do not leave the method before the actions are done
            } catch (e: ExecutionException) {
                Log.e(TAG, "Error during Say", e)
            } catch (e: CancellationException) {
                Log.i(TAG, "Interruption during Say")
            }
        }

        override fun stop() {
            if (fSay != null) {
                fSay!!.cancel(true)
            }
        }
    }

    companion object {
        private val gpt = Advisor()
    }
}

internal class Advisor {
    fun answerQuestion(question: String): String {
        val apiKey = "apikey"
        val endpoint = "https://api.openai.com/v1/chat/completions"
        val client = OkHttpClient()

        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = """{
        "model": "gpt-3.5-turbo",
        "messages": [
        {
            "role": "system",
            "content": "You are a helpful assistant."
        },
        {
            "role": "user",
            "content": "$question"
        }
        ]
    }""".trimIndent().toRequestBody(jsonMediaType)

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
            return answer!!
        }
    }
}


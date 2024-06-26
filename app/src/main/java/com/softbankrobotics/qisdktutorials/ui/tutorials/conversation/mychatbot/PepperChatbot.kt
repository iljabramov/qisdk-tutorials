package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.mychatbot

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.conversation.BaseChatbot
import com.aldebaran.qi.sdk.`object`.conversation.BaseChatbotReaction
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.ReplyPriority
import com.aldebaran.qi.sdk.`object`.conversation.SpeechEngine
import com.aldebaran.qi.sdk.`object`.conversation.StandardReplyReaction
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.softbankrobotics.qisdktutorials.utils.Constants
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException

private const val TAG = "QiChatbotActivity"

class PepperChatbot(context: QiContext?) : BaseChatbot(context) {
    private val gpt = Advisor()

    private val messages = mutableListOf(
        JSONMessage("system", "You are a helpful assistant."),
    ) // To store past messages

    override fun replyTo(phrase: Phrase, locale: Locale): StandardReplyReaction {
        if (phrase.text == "") return StandardReplyReaction(
            MyChatbotReaction(qiContext, "Please repeat"),
            ReplyPriority.NORMAL
        )
        messages.add(JSONMessage("user", phrase.text))
        val ans = gpt.answerQuestion(messages)
        messages.add(JSONMessage("assistant", ans))
        return StandardReplyReaction(
            MyChatbotReaction(qiContext, ans),
            ReplyPriority.NORMAL
        )
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
}
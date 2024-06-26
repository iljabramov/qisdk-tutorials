/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.mychatbot

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.softbankrobotics.qisdktutorials.databinding.ConversationLayoutBinding
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationBinder
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationItemType
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity
import com.softbankrobotics.qisdktutorials.utils.Constants


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

/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.motion.attachedframes

import android.os.Bundle
import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.aldebaran.qi.sdk.util.FutureUtils
import com.softbankrobotics.qisdktutorials.databinding.ActivityFollowHumanTutorialBinding
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationBinder
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationItemType
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity
import com.softbankrobotics.qisdktutorials.utils.Constants
import java.util.concurrent.TimeUnit

private const val TAG = "FollowHumanActivity"

class FollowHumanTutorialActivity : TutorialActivity<ActivityFollowHumanTutorialBinding>(),
    RobotLifecycleCallbacks {

    override fun inflateBinding(): ActivityFollowHumanTutorialBinding {
        return ActivityFollowHumanTutorialBinding.inflate(layoutInflater)
    }

    private var conversationBinder: ConversationBinder? = null

    // The QiContext provided by the QiSDK.
    private var qiContext: QiContext? = null

    // Store the FollowHumanTutorialAbility
    private var movingAbility: FollowHumanTutorialAbility? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Search humans on follow button clicked.
        binding.followButton.setOnClickListener {
            if (qiContext != null) {
                binding.followButton.isEnabled = false
                displayLine("Following in 3 seconds...", ConversationItemType.INFO_LOG)
                // Wait 3 seconds before following.
                FutureUtils.wait(3, TimeUnit.SECONDS).andThenConsume { movingAbility?.move() }
            }
        }

        // Stop moving on stop button clicked.
        binding.stopButton.setOnClickListener {
            binding.stopButton.isEnabled = false
            val message = "Stopping..."
            Log.i(TAG, message)
            displayLine(message, ConversationItemType.INFO_LOG)
            movingAbility?.stopMoving()
        }

        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        QiSDK.unregister(this, this)

        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i(TAG, "Focus gained.")
        // Store the provided QiContext.
        this.qiContext = qiContext
        // Get Moving Ability
        this.movingAbility =
            FollowHumanTutorialAbility(
                qiContext,
                waitingCallback = { enterWaitingForOrderState() },
                movingCallback = { enterMovingState() }
            )

        // Bind the conversational events to the view.
        val conversationStatus = qiContext.conversation.status(qiContext.robotContext)
        conversationBinder = binding.conversationView.bindConversationTo(conversationStatus)

        val say = SayBuilder.with(qiContext)
            .withText("Press \"Follow\" and I will follow you. Press \"Stop\" to stop me.")
            .withLocale(Constants.Locals.ENGLISH_LOCALE)
            .build()

        say.run()

        enterWaitingForOrderState()
    }

    override fun onRobotFocusLost() {
        Log.i(TAG, "Focus lost.")

        movingAbility?.terminate()
        conversationBinder?.unbind()

        // Remove the QiContext.
        this.qiContext = null
    }

    override fun onRobotFocusRefused(reason: String) {
        // Nothing here.
    }

    private fun enterWaitingForOrderState() {
        val message = "Waiting for order..."
        Log.i(TAG, message)
        displayLine(message, ConversationItemType.INFO_LOG)
        runOnUiThread {
            binding.stopButton.isEnabled = false
            binding.followButton.isEnabled = true
        }
    }

    private fun enterMovingState() {
        val message = "Moving..."
        Log.i(TAG, message)
        displayLine(message, ConversationItemType.INFO_LOG)
        runOnUiThread {
            binding.followButton.isEnabled = false
            binding.stopButton.isEnabled = true
        }
    }

    private fun displayLine(text: String, type: ConversationItemType) {
        runOnUiThread { binding.conversationView.addLine(text, type) }
    }

}

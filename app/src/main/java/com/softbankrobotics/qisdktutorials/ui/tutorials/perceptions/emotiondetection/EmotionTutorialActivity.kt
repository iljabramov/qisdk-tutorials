/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.perceptions.emotiondetection

import android.os.Bundle
import androidx.annotation.DrawableRes

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.builder.SayBuilder
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.ActivityAutonomousAbilitiesTutorialBinding
import com.softbankrobotics.qisdktutorials.databinding.ActivityEmotionTutorialBinding
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationBinder
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity
import com.softbankrobotics.qisdktutorials.utils.Constants

/**
 * The activity for the Emotion tutorial.
 */
class EmotionTutorialActivity : TutorialActivity<ActivityEmotionTutorialBinding>(), RobotLifecycleCallbacks, OnBasicEmotionChangedListener {

    override fun inflateBinding(): ActivityEmotionTutorialBinding {
        return ActivityEmotionTutorialBinding.inflate(layoutInflater)
    }
    // Store the basic emotion observer.
    private var basicEmotionObserver: BasicEmotionObserver? = null

    private lateinit var conversationBinder: ConversationBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create the basic emotion observer and listen to it.
        basicEmotionObserver = BasicEmotionObserver()
        basicEmotionObserver?.listener = this

        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    override fun onDestroy() {
        // Stop listening to basic emotion observer and remove it.
        basicEmotionObserver?.listener = null
        basicEmotionObserver = null

        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this)
        super.onDestroy()
    }
    override fun onRobotFocusGained(qiContext: QiContext) {
        // Bind the conversational events to the view.
        val conversationStatus = qiContext.conversation.status(qiContext.robotContext)
        conversationBinder = binding.conversationView.bindConversationTo(conversationStatus)

        val say = SayBuilder.with(qiContext)
            .withText("I can display the basic emotions of the human I'm seeing. Try to express an emotion with your smile, your voice or by touching my sensors.")
            .withLocale(Constants.Locals.ENGLISH_LOCALE)
            .build()

        say.run()

        // Start the basic emotion observation.
        basicEmotionObserver?.startObserving(qiContext)
    }

    override fun onRobotFocusLost() {
        conversationBinder.unbind()

        // Stop the basic emotion observation.
        basicEmotionObserver?.stopObserving()
    }

    override fun onRobotFocusRefused(reason: String) {
        // Nothing here.
    }

    override fun onBasicEmotionChanged(basicEmotion: BasicEmotion) {
        // Update basic emotion image.
        runOnUiThread { binding.emotionImageView.setImageResource(emotionImageRes(basicEmotion)) }
    }

    @DrawableRes
    private fun emotionImageRes(basicEmotion: BasicEmotion): Int {
        return when (basicEmotion) {
            BasicEmotion.UNKNOWN -> R.drawable.ic_icons_cute_anon_unknown
            BasicEmotion.NEUTRAL -> R.drawable.ic_icons_cute_anon_neutral
            BasicEmotion.CONTENT -> R.drawable.ic_icons_cute_anon_smile
            BasicEmotion.JOYFUL -> R.drawable.ic_icons_cute_anon_joyful
            BasicEmotion.SAD -> R.drawable.ic_icons_cute_anon_sad
            BasicEmotion.ANGRY -> R.drawable.ic_icons_cute_anon_anger
        }
    }
}

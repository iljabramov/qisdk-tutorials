/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.motion.attachedframes

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.builder.GoToBuilder
import com.aldebaran.qi.sdk.builder.TransformBuilder
import com.aldebaran.qi.sdk.`object`.actuation.Frame
import com.aldebaran.qi.sdk.`object`.actuation.GoTo
import com.aldebaran.qi.sdk.`object`.human.Human
import kotlin.math.sqrt

private const val TAG = "FollowHumanAbility"

class FollowHumanTutorialAbility(
    private val qiContext: QiContext,
    private val waitingCallback: () -> Unit,
    private val movingCallback: () -> Unit
) {

    // Store the action execution future.
    private var goToFuture: Future<Void>? = null

    // Store the GoTo action.
    private var goTo: GoTo? = null


    fun move() {
        searchHumans()
    }

    fun stopMoving() {
        // Cancel the GoTo action asynchronously.
        goToFuture?.requestCancellation()
    }

    fun terminate() {
        stopMoving()

        // Remove on started listeners from the GoTo action.
        goTo?.removeAllOnStartedListeners()
    }

    private fun enterWaitingForOrderState() {
        val message = "Waiting for order..."
        Log.i(TAG, message)
        waitingCallback()
    }

    private fun enterMovingState() {
        val message = "Moving..."
        Log.i(TAG, message)
        movingCallback()
    }

    private fun searchHumans() {
        val qiContext = qiContext
        val humanAwareness = qiContext.humanAwareness
        val humansAroundFuture = humanAwareness.async().humansAround
        humansAroundFuture.andThenConsume {
            // If humans found, follow the closest one.
            if (it.isNotEmpty()) {
                Log.i(TAG, "Human found.")
                val humanToFollow = getClosestHuman(it, qiContext)
                humanToFollow?.let { human -> followHuman(human) }
            } else {
                Log.i(TAG, "No human.")
                enterWaitingForOrderState()
            }
        }

    }

    private fun followHuman(human: Human) {
        // Create the target frame from the human.
        val targetFrame = createTargetFrame(human)

        // Create a GoTo action.
        val goTo = GoToBuilder.with(qiContext)
            .withFrame(targetFrame)
            .build()
            .also { this.goTo = it }

        // Update UI when the GoTo action starts.
        goTo.addOnStartedListener {
            this.enterMovingState()
        }

        this.goTo = goTo

        // Execute the GoTo action asynchronously.
        val goToFuture = goTo.async().run()

        // Update UI when the GoTo action finishes.
        goToFuture.thenConsume {
            when {
                it.isSuccess -> {
                    Log.i(TAG, "Target reached.")
                    enterWaitingForOrderState()
                }

                it.isCancelled -> {
                    Log.i(TAG, "Movement stopped.")
                    enterWaitingForOrderState()
                }

                else -> {
                    Log.e(TAG, "Movement error.", it.error)
                    enterWaitingForOrderState()
                }
            }
        }
        this.goToFuture = goToFuture
    }

    private fun createTargetFrame(humanToFollow: Human): Frame {
        // Get the human head frame.
        val humanFrame = humanToFollow.headFrame
        // Create a transform for Pepper to stay at 1 meter in front of the human.
        val transform = TransformBuilder.create().fromXTranslation(1.0)
        // Create an AttachedFrame that automatically updates with the human frame.
        val attachedFrame = humanFrame.makeAttachedFrame(transform)
        // Returns the corresponding Frame.
        return attachedFrame.frame()
    }

    private fun getClosestHuman(humans: List<Human>, qiContext: QiContext): Human? {
        // Get the robot frame.
        val robotFrame = qiContext.actuation.robotFrame()

        // Return the closest human
        return humans.minBy {
            getDistance(robotFrame, it)
        }
    }

    private fun getDistance(robotFrame: Frame, human: Human): Double {
        // Get the human head frame.
        val humanFrame = human.headFrame
        // Retrieve the translation between the robot and the human.
        val translation = humanFrame.computeTransform(robotFrame).transform.translation
        // Get the translation coordinates.
        val x = translation.x
        val y = translation.y
        // Compute and return the distance.
        return sqrt(x * x + y * y)
    }

}

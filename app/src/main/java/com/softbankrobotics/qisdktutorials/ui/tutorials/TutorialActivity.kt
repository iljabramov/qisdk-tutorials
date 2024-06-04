/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.softbankrobotics.qisdktutorials.R
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding

/**
 * Base class for a tutorial activity.
 */

private const val TAG = "TutorialActivity"

abstract class TutorialActivity<VB : ViewBinding> : RobotActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected lateinit var toolbar: Toolbar

    abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_tutorial)

        toolbar = findViewById(R.id.toolbar)
        setupToolbar()

        _binding = inflateBinding()
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        contentFrame.addView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        toolbar.findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            Log.d(TAG, "Back arrow clicked")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
                onBackPressed()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        /*val nameNotFound = -1
        val nameResId = intent.getIntExtra(Constants.Intent.TUTORIAL_NAME_KEY, nameNotFound)
        var level: TutorialLevel?

        // check if api 33 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            level = intent.getSerializableExtra(
                Constants.Intent.TUTORIAL_LEVEL_KEY,
                TutorialLevel::class.java
            ) as TutorialLevel
            if (nameResId != nameNotFound) {
                toolbar.setName(nameResId)
                toolbar.setLevel(level)
            }
        } else {
            level =
                intent.getSerializableExtra(Constants.Intent.TUTORIAL_LEVEL_KEY) as TutorialLevel
        }

        if (nameResId != nameNotFound) {
            toolbar.setName(nameResId)
            toolbar.setLevel(level)
        }*/

        toolbar.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            Log.d(TAG, "Close button clicked")
            finishAffinity()
        }

    }
}





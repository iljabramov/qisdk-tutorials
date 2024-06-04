/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.softbankrobotics.qisdktutorials.R
import androidx.viewbinding.ViewBinding
import com.softbankrobotics.qisdktutorials.model.data.TutorialLevel
import com.softbankrobotics.qisdktutorials.ui.tutorialtoolbar.TutorialToolbar
import com.softbankrobotics.qisdktutorials.utils.Constants

/**
 * Base class for a tutorial activity.
 */

private const val TAG = "TutorialActivity"

abstract class TutorialActivity<VB : ViewBinding> : RobotActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    private lateinit var toolbar: TutorialToolbar
    private lateinit var rootView: View

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null


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

    override fun onPause() {
        rootView.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        rootView = findViewById(android.R.id.content)
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight.minus(rect.bottom)

            // Hide system UI if keyboard is closed.
            if (keypadHeight <= screenHeight * 0.30) {
                hideSystemUI()
            }
        }
        rootView.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
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

        val nameNotFound = -1
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
        }

        toolbar.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            Log.d(TAG, "Close button clicked")
            finishAffinity()
        }

    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
}





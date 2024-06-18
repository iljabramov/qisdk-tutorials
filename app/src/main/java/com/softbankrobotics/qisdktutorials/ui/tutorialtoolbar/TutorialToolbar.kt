/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorialtoolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.TutorialToolbarBinding
import com.softbankrobotics.qisdktutorials.model.data.TutorialLevel

class TutorialToolbar (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Toolbar(context, attrs, defStyleAttr) {

    var binding: TutorialToolbarBinding

    init {
//        View.inflate(context, R.layout.tutorial_toolbar, this)
        binding = TutorialToolbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun setNavigationOnClickListener(listener: OnClickListener) {
        binding.backArrow.setOnClickListener(listener)
    }

    fun setName(name: String) {
        binding.titleTextview.text = name
        invalidate()
        requestLayout()
    }

    fun setName(@StringRes resId: Int) {
        binding.titleTextview.text = resources.getString((resId))
        invalidate()
        requestLayout()
    }

    fun setLevel(level: TutorialLevel) {
        when (level) {
            TutorialLevel.BASIC -> {
                binding.levelTextview.setText(R.string.toolbar_basic_level)
                binding.backgroundView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.basic_green
                    )
                )
            }
            TutorialLevel.ADVANCED -> {
                binding.levelTextview.setText(R.string.toolbar_advanced_level)
                binding.backgroundView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.advanced_orange
                    )
                )
            }
        }

        invalidate()
        requestLayout()
    }
}

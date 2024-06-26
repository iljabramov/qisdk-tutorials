/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.bilateralswitch

import android.content.Context
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.BilateralSwitchBinding

private val BACKGROUND_FIRST_SECTION_COLOR = R.color.basic_green
private val BACKGROUND_SECOND_SECTION_COLOR = R.color.advanced_orange

private const val TRANSITION_DURATION = 100

private val FIRST_SECTION_TEXT = R.string.basic_level
private val SECOND_SECTION_TEXT = R.string.advanced_level

class BilateralSwitch(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private lateinit var binding: BilateralSwitchBinding
    // only valid between inflate and onDestroyView

    private var allowClick = true
    private var isChecked = false
    private var shouldNotifyListener = true

    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var firstSectionName: String? = null
    private var secondSectionName: String? = null


    init {
        if (attrs != null) {
            getAttributes(context, attrs)
        }

        inflateLayout()
    }

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)

    private fun getAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BilateralSwitch, 0, 0)

        firstSectionName = typedArray.getString(R.styleable.BilateralSwitch_first_section_name)
        secondSectionName = typedArray.getString(R.styleable.BilateralSwitch_second_section_name)
    }

    private fun inflateLayout() {
//        LayoutInflater.from(context).inflate(R.layout.bilateral_switch, this, true)
        binding = BilateralSwitchBinding.inflate(LayoutInflater.from(context), this, true)

        setOnClickListener(this)

        binding.levelView.text = resources.getString(FIRST_SECTION_TEXT)
        binding.colorLayer.setBackgroundColor(
            ContextCompat.getColor(
                context,
                BACKGROUND_FIRST_SECTION_COLOR
            )
        )

        binding.firstSection.text = firstSectionName
        binding.secondSection.text = secondSectionName
    }

    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }


    override fun onClick(view: View) {
        if (!allowClick) {
            return
        }

        allowClick = false

        binding.buttonHover.visibility = View.VISIBLE
        collapseTransition()
    }

    fun setChecked(checked: Boolean) {
        if (isChecked != checked && allowClick) {
            shouldNotifyListener = false
            onClick(this)
        }
    }

    private fun collapseTransition() {
        val cs = ConstraintSet()
        val layout: ConstraintLayout = binding.layout
        cs.clone(layout)

        if (!isChecked) {
            cs.connect(R.id.color_layer, ConstraintSet.START, R.id.level_view, ConstraintSet.START)
            cs.connect(R.id.color_layer, ConstraintSet.END, R.id.level_view, ConstraintSet.END)
        } else {
            cs.connect(R.id.color_layer, ConstraintSet.END, R.id.level_view, ConstraintSet.END)
            cs.connect(R.id.color_layer, ConstraintSet.START, R.id.level_view, ConstraintSet.START)
        }


        val firstTransition = AutoTransition()
        firstTransition.duration = TRANSITION_DURATION.toLong()

        firstTransition.addEndListener { transition, listener ->
            binding.buttonHover.visibility = View.GONE

            if (isChecked) {
                binding.levelView.text = resources.getString(FIRST_SECTION_TEXT)
            } else {
                binding.levelView.text = resources.getString(SECOND_SECTION_TEXT)
            }

            transition.removeListener(listener)
            expandTransition()
        }
        TransitionManager.beginDelayedTransition(
                this,
                firstTransition)

        cs.applyTo(layout)
    }

    private fun expandTransition() {
        val cs = ConstraintSet()
        val layout: ConstraintLayout = binding.layout
        cs.clone(layout)

        if (!isChecked) {
            cs.connect(R.id.color_layer, ConstraintSet.END, R.id.second_section, ConstraintSet.END)
            cs.connect(R.id.color_layer, ConstraintSet.START, R.id.level_view, ConstraintSet.START)
            binding.colorLayer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    BACKGROUND_SECOND_SECTION_COLOR
                )
            )
        } else {
            cs.connect(R.id.color_layer, ConstraintSet.END, R.id.level_view, ConstraintSet.END)
            cs.connect(
                R.id.color_layer,
                ConstraintSet.START,
                R.id.first_section,
                ConstraintSet.START
            )
            binding.colorLayer.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    BACKGROUND_FIRST_SECTION_COLOR
                )
            )
        }

        val firstTransition = AutoTransition()
        firstTransition.duration = TRANSITION_DURATION.toLong()

        firstTransition.addEndListener { transition, listener ->
            isChecked = !isChecked
            transition.removeListener(listener)
            allowClick = true

            if (onCheckedChangeListener != null && shouldNotifyListener) {
                onCheckedChangeListener?.onCheckedChanged(isChecked)
            }

            shouldNotifyListener = true
        }

        TransitionManager.beginDelayedTransition(
                this,
                firstTransition)

        cs.applyTo(layout)
    }

    private inline fun TransitionSet.addEndListener(crossinline listener: (Transition, Transition.TransitionListener) -> Unit) {
        this.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                listener(transition, this)
            }

            override fun onTransitionResume(transition: Transition) {}

            override fun onTransitionPause(transition: Transition) {}

            override fun onTransitionCancel(transition: Transition) {}

            override fun onTransitionStart(transition: Transition) {}

        })
    }
}

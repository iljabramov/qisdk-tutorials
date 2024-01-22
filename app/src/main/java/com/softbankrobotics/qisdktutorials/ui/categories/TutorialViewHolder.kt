/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.categories

import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.TutorialLayoutBinding
import com.softbankrobotics.qisdktutorials.model.data.Tutorial
import com.softbankrobotics.qisdktutorials.model.data.TutorialLevel

/**
 * The view holder to show a tutorial.
 */
internal class TutorialViewHolder(
    private val itemBinding: TutorialLayoutBinding,
    private val onTutorialClickedListener: OnTutorialClickedListener
) : RecyclerView.ViewHolder(itemBinding.root) {

    /**
     * Binds a tutorial to the views.
     * @param tutorial the tutorial to bind
     */
    fun bind(tutorial: Tutorial) {


        with(itemBinding.radioButton) {
            isChecked = tutorial.isSelected
            isEnabled = tutorial.isEnabled
            text = "\"${itemView.context.getString(tutorial.nameResId)}\""
            setOnClickListener {
                onTutorialClickedListener.onTutorialClicked(tutorial)
            }
        }

        val tutorialLevel = tutorial.tutorialLevel
        bindLevelView(tutorialLevel)
    }

    /**
     * Bind the level view.
     * @param tutorialLevel the tutorial level
     */
    private fun bindLevelView(tutorialLevel: TutorialLevel) {
        when (tutorialLevel) {
            TutorialLevel.BASIC -> {
                itemBinding.levelTextview.setText(R.string.basic_level)
                itemBinding.levelTextview.setBackgroundResource(R.drawable.basic_level_shape)
            }
            TutorialLevel.ADVANCED -> {
                itemBinding.levelTextview.setText(R.string.advanced_level)
                itemBinding.levelTextview.setBackgroundResource(R.drawable.advanced_level_shape)
            }
        }
    }
}

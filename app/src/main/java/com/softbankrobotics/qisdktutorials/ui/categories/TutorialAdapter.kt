/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.databinding.TutorialLayoutBinding
import com.softbankrobotics.qisdktutorials.model.data.Tutorial

/**
 * The adapter used to show tutorials.
 */
internal class TutorialAdapter(private val onTutorialClickedListener: OnTutorialClickedListener) : RecyclerView.Adapter<TutorialViewHolder>() {

    private var tutorials = listOf<Tutorial>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val itemBinding =
            TutorialLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.tutorial_layout, parent, false)
        return TutorialViewHolder(itemBinding, onTutorialClickedListener)
    }

    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        val tutorial = tutorials[position]
        holder.bind(tutorial)
    }

    override fun getItemCount() = tutorials.size

    /**
     * Update the tutorials list.
     * @param tutorials the tutorials
     */
    fun updateTutorials(tutorials: List<Tutorial>) {
        this.tutorials = tutorials
        notifyDataSetChanged()
    }

    /**
     * Select the specified tutorial.
     * @param tutorial the tutorial
     */
    fun selectTutorial(tutorial: Tutorial) {
        tutorial.isSelected = true
        notifyDataSetChanged()
    }

    /**
     * Unselect all the tutorials.
     */
    fun unselectTutorials() {
        tutorials.forEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    /**
     * Enable / disable all the tutorials.
     * @param enabled `true` to enable, `false` to disable
     */
    fun setTutorialsEnabled(enabled: Boolean) {
        tutorials.forEach {
            it.isEnabled = enabled
        }
        notifyDataSetChanged()
    }
}

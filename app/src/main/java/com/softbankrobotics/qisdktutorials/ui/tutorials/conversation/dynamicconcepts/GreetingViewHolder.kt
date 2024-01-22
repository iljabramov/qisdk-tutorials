/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.dynamicconcepts

import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.databinding.GreetingLayoutBinding

/**
 * The view holder to show a greeting.
 */
internal class GreetingViewHolder(
    private val itemBinding: GreetingLayoutBinding,
    private val onGreetingRemovedListener: OnGreetingRemovedListener?
) : RecyclerView.ViewHolder(itemBinding.root) {

    /**
     * Binds a tutorial to the views.
     * @param greeting the greeting
     */
    fun bind(greeting: String) {
        itemBinding.greetingTextview.text = greeting
        itemBinding.deleteButton.setOnClickListener {
            onGreetingRemovedListener?.onGreetingRemoved(
                greeting
            )
        }
    }
}

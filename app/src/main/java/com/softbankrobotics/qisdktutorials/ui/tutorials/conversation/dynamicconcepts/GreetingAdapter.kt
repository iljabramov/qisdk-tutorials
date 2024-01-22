/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.conversation.dynamicconcepts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.databinding.GreetingLayoutBinding

/**
 * The adapter used to show greetings.
 */
internal class GreetingAdapter(private val onGreetingRemovedListener: OnGreetingRemovedListener) : RecyclerView.Adapter<GreetingViewHolder>() {

    private val greetings: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GreetingViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.greeting_layout, parent,false)
        val itemBinding =
            GreetingLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GreetingViewHolder(itemBinding, onGreetingRemovedListener)
    }

    override fun onBindViewHolder(holder: GreetingViewHolder, position: Int) {
        val greeting = greetings[position]
        holder.bind(greeting)
    }

    override fun getItemCount(): Int = greetings.size

    fun addGreeting(greeting: String) {
        greetings.add(greeting)
        notifyItemInserted(greetings.size - 1)
    }

    fun removeGreeting(greeting: String) {
        val index = greetings.indexOf(greeting)
        if (index != -1) {
            greetings.remove(greeting)
            notifyItemRemoved(index)
        }
    }

    fun containsGreeting(greeting: String): Boolean = greetings.contains(greeting)

}

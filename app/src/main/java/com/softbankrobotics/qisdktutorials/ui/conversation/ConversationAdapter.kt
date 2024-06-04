/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.conversation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.LayoutInfoLogViewBinding
import kotlin.collections.mutableListOf
private const val INFO_LOG_VIEW_TYPE = 0
private const val ERROR_LOG_VIEW_TYPE = 1
private const val ROBOT_OUTPUT_VIEW_TYPE = 2
private const val HUMAN_INPUT_VIEW_TYPE = 3

/**
 * Adapter for the conversation view.
 */
internal class ConversationAdapter : RecyclerView.Adapter<ConversationViewHolder>() {

    private val items = mutableListOf<ConversationItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemBinding =
            LayoutInfoLogViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversationItem = items[position]
        holder.bind(conversationItem.text)
    }

    override fun getItemCount() = items.size


    override fun getItemViewType(position: Int): Int {
        val conversationItem = items[position]
        val type = conversationItem.type.name
        return when (type) {
            ConversationItemType.INFO_LOG.name -> INFO_LOG_VIEW_TYPE
            ConversationItemType.ERROR_LOG.name -> ERROR_LOG_VIEW_TYPE
            ConversationItemType.HUMAN_INPUT.name -> HUMAN_INPUT_VIEW_TYPE
            ConversationItemType.ROBOT_OUTPUT.name -> ROBOT_OUTPUT_VIEW_TYPE
            else -> throw IllegalArgumentException("Unknown conversation item type: $type")
        }
    }
    /**
     * Add an item to the view.
     * @param text the item text
     * @param type the item type
     */
    fun addItem(text: String, type: ConversationItemType) {
        items.add(ConversationItem(text, type))
        notifyItemInserted(items.size - 1)
    }

    @LayoutRes
    private fun layoutFromViewType(viewType: Int): Int {
        return when (viewType) {
            INFO_LOG_VIEW_TYPE -> R.layout.layout_info_log_view
            ERROR_LOG_VIEW_TYPE -> R.layout.layout_error_log_view
            ROBOT_OUTPUT_VIEW_TYPE -> R.layout.layout_robot_output_view
            HUMAN_INPUT_VIEW_TYPE -> R.layout.layout_human_input_view
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }
}

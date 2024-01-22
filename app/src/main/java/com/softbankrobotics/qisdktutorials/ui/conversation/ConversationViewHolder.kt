/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.conversation

import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.databinding.LayoutInfoLogViewBinding

/**
 * View holder for the conversation view.
 */
internal class ConversationViewHolder(private val itemBinding: LayoutInfoLogViewBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    /**
     * Bind the text to the view.
     * @param text the text
     */
    fun bind(text: String) {
        itemBinding.textview.text = text
    }
}

/*
 * Copyright (C) 2018 Softbank Robotics Europe
 * See COPYING for the license
 */

package com.softbankrobotics.qisdktutorials.ui.tutorials.perceptions.humanawareness

import androidx.recyclerview.widget.RecyclerView
import com.softbankrobotics.qisdktutorials.R
import com.softbankrobotics.qisdktutorials.databinding.HumanInfoLayoutBinding

/**
 * The view holder to show human information.
 */
internal class HumanInfoViewHolder(private val itemBinding: HumanInfoLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {

    /**
     * Binds human information to the views.
     *
     * @param humanInfo the human information to bind
     */
    fun bind(humanInfo: HumanInfo) {
        val itemView = itemBinding.root
        val resources = itemView.resources
        itemBinding.ageTextview.text =
            resources.getQuantityString(R.plurals.age, humanInfo.age, humanInfo.age)
        itemBinding.genderTextview.text = resources.getString(R.string.gender, humanInfo.gender)
        itemBinding.pleasureStateTextview.text =
            resources.getString(R.string.pleasure_state, humanInfo.pleasureState)
        itemBinding.excitementStateTextview.text =
            resources.getString(R.string.excitement_state, humanInfo.excitementState)
        itemBinding.engagementIntentionStateTextview.text = resources.getString(
            R.string.engagement_intention_state,
            humanInfo.engagementIntentionState
        )
        itemBinding.smileStateTextview.text =
            resources.getString(R.string.smile_state, humanInfo.smileState)
        itemBinding.attentionStateTextview.text =
            resources.getString(R.string.attention_state, humanInfo.attentionState)
        itemBinding.distanceTextview.text =
            resources.getString(R.string.distance, humanInfo.distance)
        //we should put image bitmap to null to avoid setting image on recycled bitmap
        itemBinding.faceImageview.setImageBitmap(null)
        if (humanInfo.facePicture == null) {
            itemBinding.faceImageview.setBackgroundResource(R.drawable.ic_icons_cute_anon_unknown)
        } else {
            itemBinding.faceImageview.setImageBitmap(humanInfo.facePicture)
        }
    }
}

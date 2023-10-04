package com.skillbox.ascent.ui.activities.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.skillbox.ascent.data.activity.DetailedActivityWithAvatar

class ActivitiesListAdapter(
) : AsyncListDifferDelegationAdapter<DetailedActivityWithAvatar>(ActivityDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(ActivitiesListAdapterDelegate())
    }

    class ActivityDiffUtilCallback : DiffUtil.ItemCallback<DetailedActivityWithAvatar>() {
        override fun areItemsTheSame(
            oldItem: DetailedActivityWithAvatar,
            newItem: DetailedActivityWithAvatar
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DetailedActivityWithAvatar,
            newItem: DetailedActivityWithAvatar
        ): Boolean {
            return oldItem == newItem
        }
    }

}
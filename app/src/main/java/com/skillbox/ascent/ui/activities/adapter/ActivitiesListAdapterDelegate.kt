package com.skillbox.ascent.ui.activities.adapter

import android.content.Context

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.skillbox.ascent.R
import com.skillbox.ascent.data.TypeActivityEnum
import com.skillbox.ascent.data.activity.DetailedActivityWithAvatar
import com.skillbox.ascent.databinding.ItemActivityBinding
import com.skillbox.ascent.utils.inflate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ActivitiesListAdapterDelegate : AbsListItemAdapterDelegate<DetailedActivityWithAvatar,
        DetailedActivityWithAvatar,
        ActivitiesListAdapterDelegate.Holder>() {

    override fun isForViewType(
        item: DetailedActivityWithAvatar,
        items: MutableList<DetailedActivityWithAvatar>,
        position: Int
    ): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(parent.inflate(ItemActivityBinding::inflate))
    }

    override fun onBindViewHolder(
        item: DetailedActivityWithAvatar,
        holder: Holder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class Holder(
        private val binding: ItemActivityBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: DetailedActivityWithAvatar) {
            val context = binding.cardActivity.context
            val actual =
                OffsetDateTime.parse(activity.startDateLocal, DateTimeFormatter.ISO_DATE_TIME)
            val formatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm")
            val formatDateTime = actual.format(formatter)
            with(binding) {
                //если тренировка из отложенных , то выделяем в списке
                if (activity.createdFromLazy) {
                    yetNotCreatedTextView.isVisible = true
                    activitiesConstraint.background =
                        ContextCompat.getDrawable(context, R.color.snack_failed_back)
                } else {
                    yetNotCreatedTextView.isVisible = false
                    activitiesConstraint.background =
                        ContextCompat.getDrawable(context, R.color.cardview_light_background)
                }

                nameTextView.text = activity.name
                dateTimeTextView.text = formatDateTime
                typeTextView.text = translateTypeIfRu(activity.type)
                "${activity.totalElevationGain} ${context.resources.getString(R.string.min_)}"
                    .also { valueElevationTextView.text = it }
                "${(activity.distance / 1000)} ${context.resources.getString(R.string.km)}"
                    .also { valueDistanceTextView.text = it }
                valueTimeTextView.text = timeToString(activity.elapsedTime, context)
                Glide.with(itemAvatarView)
                    .load(activity.avatarUrl)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_error)
                    .into(itemAvatarView)
            }
        }

        private fun translateTypeIfRu(typeResponse: String): String {
            var type = typeResponse
            if (Locale.getDefault().language == "ru") {
                TypeActivityEnum.values().forEach {
                    if (it.lang.first == typeResponse) type = it.lang.second
                }
            }
            return type
        }

        private fun timeToString(secs: Int, context: Context): CharSequence {
            val hour = secs / 3600
            val min = secs / 60 % 60
            return String.format(
                "%02d${context.resources.getString(R.string.hour_)} %02d${
                    context.resources.getString(R.string.min_)
                }", hour, min
            )
        }
    }
}

package com.skillbox.ascent.data.activity

data class DetailedActivityWithAvatar(
    val id: String,
    val name: String,
    val type: String,
    val startDateLocal: String,
    val elapsedTime: Int,
    val description: String?,
    val distance: Float,
    val trainer: Boolean,
    val commute: Boolean,
    val avatarUrl: String?,
    val totalElevationGain: Float,
    val createdFromLazy: Boolean
)

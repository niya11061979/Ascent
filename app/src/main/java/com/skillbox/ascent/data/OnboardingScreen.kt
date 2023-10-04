package com.skillbox.ascent.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingScreen(
    @StringRes val skipTextRes: Int,
    @StringRes val titleTextRes: Int,
    @StringRes val descriptionTextRes: Int,
    @DrawableRes val drawableRes: Int
)

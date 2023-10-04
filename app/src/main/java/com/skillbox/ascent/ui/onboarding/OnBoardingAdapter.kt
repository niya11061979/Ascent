package com.skillbox.ascent.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skillbox.ascent.data.OnboardingScreen

class OnBoardingAdapter(
    private val screen: List<OnboardingScreen>,
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount() = screen.size
    override fun createFragment(position: Int): Fragment {
        val screen: OnboardingScreen = screen[position]
        return OnBoardingFragment.newInstance(
            skipTextRes = screen.skipTextRes,
            titleTextRes = screen.titleTextRes,
            descriptionTextRes = screen.descriptionTextRes,
            drawableRes = screen.drawableRes
        )
    }
}
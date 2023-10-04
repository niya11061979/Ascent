package com.skillbox.ascent.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.skillbox.ascent.R
import com.skillbox.ascent.data.OnboardingScreen
import com.skillbox.ascent.interfaces.SkipListener
import com.skillbox.ascent.main.MainActivity
import com.skillbox.ascent.utils.VerticalFlipTransformation
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class OnBoardingActivity : AppCompatActivity(), SkipListener {
    private val depthTransformation = VerticalFlipTransformation()

    private val screens = listOf(
        OnboardingScreen(
            skipTextRes = R.string.skip,
            titleTextRes = R.string.welcome,
            descriptionTextRes = R.string.description_welcome,
            drawableRes = R.drawable.image_welcome
        ),
        OnboardingScreen(
            skipTextRes = R.string.skip,
            titleTextRes = R.string.friends,
            descriptionTextRes = R.string.description_friends,
            drawableRes = R.drawable.image_friends
        ),
        OnboardingScreen(
            skipTextRes = R.string.okay,
            titleTextRes = R.string.activities,
            descriptionTextRes = R.string.description_activities,
            drawableRes = R.drawable.image_activities
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Ascent)
        super.onCreate(savedInstanceState)
        Log.d("main1", "OnBoardingActivity onCreate ")
        setContentView(R.layout.activity_onboarding)
        val adapter = OnBoardingAdapter(screens, this)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = adapter
        val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        wormDotsIndicator.setViewPager2(viewPager)
        viewPager.setPageTransformer(depthTransformation)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        backToMainActivity()
    }


    private fun backToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun skip() {
        backToMainActivity()
    }
}
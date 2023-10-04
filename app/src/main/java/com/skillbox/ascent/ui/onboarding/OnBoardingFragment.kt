package com.skillbox.ascent.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.skillbox.ascent.databinding.FragmentOnboardingBinding
import com.skillbox.ascent.interfaces.SkipListener
import com.skillbox.ascent.utils.ViewBindingFragment
import com.skillbox.ascent.utils.withArguments


class OnBoardingFragment :
    ViewBindingFragment<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {
    private var skip: SkipListener? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResource()
        binding.skipTextView.setOnClickListener { skip?.skip() }
    }


    private fun setResource() {
        binding.skipTextView.setText(requireArguments().getInt(KEY_SKIP))
        binding.titleScreen.setText(requireArguments().getInt(KEY_TITLE))
        binding.descriptionScreen.setText(requireArguments().getInt(KEY_DESCRIPTION))
        binding.imageScreen.setImageResource(requireArguments().getInt(KEY_IMAGE))

    }

    companion object {
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_TITLE = "title"
        private const val KEY_SKIP = "backPressed"
        private const val KEY_IMAGE = "image"
        fun newInstance(
            @StringRes skipTextRes: Int,
            @StringRes titleTextRes: Int,
            @StringRes descriptionTextRes: Int,
            @DrawableRes drawableRes: Int
        ): OnBoardingFragment {
            return OnBoardingFragment().withArguments {
                putInt(KEY_DESCRIPTION, descriptionTextRes)
                putInt(KEY_TITLE, titleTextRes)
                putInt(KEY_SKIP, skipTextRes)
                putInt(KEY_IMAGE, drawableRes)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            skip = activity as SkipListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString())
        }
    }
}

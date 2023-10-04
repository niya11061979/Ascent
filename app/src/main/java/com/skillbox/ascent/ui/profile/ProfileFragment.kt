package com.skillbox.ascent.ui.profile


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skillbox.ascent.R
import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.databinding.FragmentProfileBinding
import com.skillbox.ascent.interfaces.OnBackPress
import com.skillbox.ascent.interfaces.PickerInterface
import com.skillbox.ascent.ui.custompicker.WeightDialogFragment
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment :
    ViewBindingFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate), PickerInterface {
    private val viewModel: ProfileViewModel by viewModels()
    private var flagLoadFromDb = false
    private lateinit var toolbar: Toolbar
    private var backPress: OnBackPress? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolBar)
        toolbar.title = getString(R.string.profile)
        bindViewModel()
    }


    private fun bindViewModel() {
        val view = binding.profileScroll
        listenerMenuToolBar(toolbar)
        toolbar.setNavigationOnClickListener { confirmDeauthDialog() }
        binding.weightText.setOnClickListener {
            WeightDialogFragment().show(childFragmentManager, TAG_WEIGHT)

        }
        binding.shareButton.setOnClickListener {
            findNavController().navigate(R.id.shareFragment)
        }
        binding.deauthorizeButton.setOnClickListener { confirmDeauthDialog() }

        viewModel.valueSharedPrefLiveData.observe(viewLifecycleOwner) { accessToken ->
            viewModel.deauthorization(accessToken)
        }
        viewModel.dataCleared.observe(viewLifecycleOwner) { isCacheClean ->
            if (isCacheClean) {
                snackBarSuccess(
                    view, resources.getString(R.string.cache_clean),
                    searchBottomView(requireActivity())

                )

            } else
                snackBarFailed(
                    view, resources.getString(R.string.cache_not_clean),
                    searchBottomView(requireActivity())
                )
            backPress?.onBackPressed()
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            snackBarFailed(view, it, searchBottomView(requireActivity()))
            flagLoadFromDb = true
            viewModel.loadAthleteFromDb()
        }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
            binding.profileProgressBar.isVisible = it
        }
        viewModel.profileLiveData.observe(viewLifecycleOwner) { setProfileData(it) }
        viewModel.weightEditLiveData.observe(viewLifecycleOwner) {
            snackBarSuccess(
                view, getString(R.string.success_edit),
                searchBottomView(requireActivity())
            )
        }

    }

    private fun setProfileData(profile: DetailedAthlete) {
        val avatarProfile = binding.avatarImageView
        binding.countFollowerTextView.text = profile.countFollower.toString()
        binding.countFollowingTextView.text = profile.countFollowing.toString()
        binding.valueGenderTextView.text = profile.gender
        ("${profile.weight} ${getString(R.string.kg)}")
            .also { binding.weightText.setText(it) }
        "${profile.firstName} ${profile.lastName}".also { binding.nameTextView.text = it }
        binding.valueCountryTextView.text = profile.country
        Glide.with(avatarProfile)
            .load(profile.avatarUrl)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_error)
            .into(avatarProfile)
    }

    private fun confirmDeauthDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.confirm))
            .setMessage(resources.getString(R.string.supporting_text))
            .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                viewModel.getAccessTokenFromShared() //берем access_token из pref

            }
            .show()
    }

    override fun getValue(key: Int, value: String) {
        if (key == 1) ("$value ${getString(R.string.kg)}").also {
            binding.weightTextField.editText?.setText(it)
        }

        viewModel.editWeightAthlete(value.toFloat())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            backPress = requireActivity() as OnBackPress
        } catch (t: ClassCastException) {
            throw ClassCastException(activity.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        statusBottomBar(requireActivity(), true)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isRemoving) {
            requireActivity().hideKeyboardAndClearFocus()
        }
    }


    companion object {
        const val SHARED_PREF_NAME = "auth_access_token" //для Url  и названий файлов
        const val APP_SETTING = "app_setting"       //для сохранения настроек(первый запуск)
        const val TAG_WEIGHT = "weight"
        const val KEY_ACCESS_TOKEN = "access_token"
    }



}




package com.skillbox.ascent.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentAuthBinding
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class AuthFragment : ViewBindingFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {
    private val viewModel: AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = setColor(requireContext(), R.color.auth_status_color)
        bindViewModel()
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            if (it.resultCode == RESULT_OK && data != null) {
                val tokenExchangeRequest = AuthorizationResponse.fromIntent(data)
                    ?.createTokenExchangeRequest()
                val exception = AuthorizationException.fromIntent(data)
                when {
                    tokenExchangeRequest != null && exception == null ->
                        viewModel.onAuthCodeReceived(tokenExchangeRequest)
                    exception != null -> viewModel.onAuthCodeFailed()
                }
            }
        }

    private fun bindViewModel() {
        binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) {
            findNavController()
                .navigate(AuthFragmentDirections.actionAuthFragmentToProfileFragment())
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) { toast(it) }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        binding.loginButton.isVisible = !isLoading
        binding.loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) = getResult.launch(intent)


    override fun onResume() {
        super.onResume()
        Log.d("AuthFragment", "Auth onResume ")
        statusBottomBar(requireActivity(), false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("AuthFragment", "Auth onDestroyView ")
        activity?.window?.statusBarColor = setColor(requireContext(), R.color.purple_700)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AuthFragment", "Auth onDestroy ")
        if (isRemoving) {
            requireActivity().hideKeyboardAndClearFocus()
        }
    }

}
package com.skillbox.ascent.ui.share

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skillbox.ascent.R
import com.skillbox.ascent.data.contact.Contact
import com.skillbox.ascent.databinding.FragmentShareBinding
import com.skillbox.ascent.ui.share.adapter.ContactListAdapter
import com.skillbox.ascent.ui.sms.SmsSendFragment
import com.skillbox.ascent.ui.sms.SmsSendFragment.Companion.TAG
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest


@AndroidEntryPoint
class ShareFragment : ViewBindingFragment<FragmentShareBinding>(FragmentShareBinding::inflate) {
    private var contactAdapter: ContactListAdapter by autoCleared()
    private val viewModel by viewModels<ContactListViewModel>()
    private lateinit var toolbar: Toolbar
    private var idAthlete = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolBar)
        toolbar.title = getString(R.string.shareButtonText)
        initList()
        Handler(Looper.getMainLooper()).post {
            constructPermissionsRequest(
                Manifest.permission.READ_CONTACTS,
                onShowRationale = ::onContactPermissionShowRationale,
                onPermissionDenied = ::onContactPermissionDenied,
                onNeverAskAgain = ::onContactPermissionNeverAskAgain,
                requiresPermission = { viewModel.loadContacts() }
            )
                .launch()
        }
        bindViewModel()
    }

    private fun bindViewModel() { //
        listenerMenuToolBar(toolbar)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
            binding.listProgressBar.isVisible = it
        }
        viewModel.contactsLiveData.observe(viewLifecycleOwner) { contactAdapter.items = it }
        viewModel.idAthleteLiveData.observe(viewLifecycleOwner) { idAthlete = it.toString() }
    }

    private fun initList() {
        contactAdapter = ContactListAdapter(::clickedContact)
        with(binding.contactListRecyclerView) {
            setHasFixedSize(true)
            adapter = contactAdapter
            setHasFixedSize(true)
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            itemAnimator = ScaleInAnimator()  //применим аниматор
        }
    }

    private fun clickedContact(contact: Contact) {
        viewModel.getIdAthleteId()//чтобы взять ID спортсмена для отправки
        //если номеров больше 1, то предлагаем выбрать
        if (contact.phones.size > 1) {
            val phones = contact.phones.toTypedArray()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.choice_tel))
                .setItems(phones) { _, which -> showSendSmsDialog(phones[which]) }.show()
        } else {
            showSendSmsDialog(contact.phones[0])
        }
    }

    private fun showSendSmsDialog(telNumber: String) {
        Handler(Looper.getMainLooper()).post {
            constructPermissionsRequest(
                Manifest.permission.SEND_SMS,
                onShowRationale = ::onContactPermissionShowRationale,
                onPermissionDenied = ::onContactPermissionDenied,
                onNeverAskAgain = ::onContactPermissionNeverAskAgain,
                requiresPermission = {
                    val smsSend = SmsSendFragment().withArguments {
                        putStringArray(KEY_TEL_NUMBER, arrayOf(telNumber, idAthlete))
                    }
                    smsSend.show(childFragmentManager, TAG)
                }
            )
                .launch()
        }
    }

    private fun onContactPermissionDenied() {
        toast(R.string.contact_list_permission_denied)
    }

    private fun onContactPermissionShowRationale(request: PermissionRequest) {
        request.proceed()
    }

    private fun onContactPermissionNeverAskAgain() {
        toast(R.string.contact_add_permission)
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
        const val KEY_TEL_NUMBER = "tel_number"
    }
}



package com.skillbox.ascent.ui.sms

import android.os.Bundle
import android.telephony.SmsManager.getDefault
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.skillbox.ascent.R
import com.skillbox.ascent.ui.share.ShareFragment.Companion.KEY_TEL_NUMBER

class SmsSendFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundleFromShare = requireArguments().getStringArray(KEY_TEL_NUMBER)
        val telNumberText = view.findViewById<TextInputEditText>(R.id.telNumberText)
        val textSms = view.findViewById<TextInputEditText>(R.id.textSms)
        val sendButton = view.findViewById<AppCompatButton>(R.id.sendButton)
        val telNumber = bundleFromShare?.get(0)
        val msg = "${resources.getString(R.string.already_in)}${bundleFromShare?.get(1)}"
        telNumberText.setText(telNumber)
        textSms.setText(msg)
        sendButton.setOnClickListener {
            try {
                getDefault().sendTextMessage(
                    telNumber,
                    null,
                    msg,
                    null,
                    null
                )
            } catch (t: Throwable) {
                Log.d("msg", "----sms-----${t.message}")
            }
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
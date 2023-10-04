package com.skillbox.ascent.ui.custompicker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.skillbox.ascent.R
import com.skillbox.ascent.interfaces.PickerInterface

class ElapcedDialogFragment : DialogFragment() {
    private var hour = 0
    private var min = 0
    private var sec = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.fragment_elapce_picker, null)
        builder.setView(view)
        val elapsedHour = view?.findViewById<NumberPicker>(R.id.elapseHour)
        val elapsedMin = view?.findViewById<NumberPicker>(R.id.elapseMin)
        val elapseSec = view?.findViewById<NumberPicker>(R.id.elapseSec)
        val elapsedButton = view?.findViewById<Button>(R.id.elapsedButton)
        elapsedHour?.minValue = 0
        elapsedHour?.maxValue = 24
        elapsedMin?.minValue = 0
        elapsedMin?.maxValue = 59
        elapseSec?.minValue = 0
        elapseSec?.maxValue = 59
        elapsedHour?.setOnValueChangedListener { _, _, i2 -> hour = i2 }
        elapsedMin?.setOnValueChangedListener { _, _, i2 -> min = i2 }
        elapseSec?.setOnValueChangedListener { _, _, i2 -> sec = i2 }
        elapsedButton?.setOnClickListener {
            val listenerAdd = parentFragment as PickerInterface?
            listenerAdd?.getValue(3, "$hour/$min/$sec")
            dismiss()
        }
        return builder.create()
    }


}

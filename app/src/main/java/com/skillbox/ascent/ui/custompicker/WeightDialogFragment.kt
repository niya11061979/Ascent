package com.skillbox.ascent.ui.custompicker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.skillbox.ascent.interfaces.PickerInterface
import com.skillbox.ascent.R

class WeightDialogFragment : DialogFragment(R.layout.fragment_weight_picker) {
    private var kg = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.fragment_weight_picker, null)
        builder.setView(view)
        val weightKg = view?.findViewById<NumberPicker>(R.id.weightKg)
        val weightEditButton = view?.findViewById<AppCompatButton>(R.id.weightEditButton)
        weightKg?.minValue = 30
        weightKg?.maxValue = 150
        weightKg?.setOnValueChangedListener { _, _, i2 -> kg = i2 }
        weightEditButton?.setOnClickListener {
            val listenerAdd = parentFragment as PickerInterface?
            listenerAdd?.getValue(1, kg.toString())
            dismiss()
        }
        return builder.create()
    }

}
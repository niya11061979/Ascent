package com.skillbox.ascent.ui.custompicker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.skillbox.ascent.R
import com.skillbox.ascent.interfaces.PickerInterface

class DistanceDialogFragment : DialogFragment() {
    private var distanceKm = 0
    private var distanceM = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.fragment_distance_picker, null)
        builder.setView(view)
        val distanceKilometer = view?.findViewById<NumberPicker>(R.id.distanceKilometer)
        val distanceMetre = view?.findViewById<NumberPicker>(R.id.distanceMetre)
        val distanceButton = view?.findViewById<Button>(R.id.distanceButton)
        distanceKilometer?.minValue = 0
        distanceKilometer?.maxValue = 99
        distanceMetre?.minValue = 0
        distanceMetre?.maxValue = 9
        distanceKilometer?.setOnValueChangedListener { _, _, i2 -> distanceKm = i2 }
        distanceMetre?.setOnValueChangedListener { _, _, i2 -> distanceM = i2 }

        distanceButton?.setOnClickListener {
            val listenerAddDistance = parentFragment as PickerInterface?
            listenerAddDistance?.getValue(2, "$distanceKm/$distanceM")
            dismiss()
        }

        return builder.create()

    }

}

package com.skillbox.ascent.ui.alert

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.skillbox.ascent.R
import com.skillbox.ascent.data.StateAlert
import com.skillbox.ascent.databinding.FragmentAlertSetBinding
import com.skillbox.ascent.interfaces.StateAlertInterface
import com.skillbox.ascent.utils.ViewBindingFragment
import com.skillbox.ascent.utils.snackBar
import com.skillbox.ascent.utils.statusBottomBar
import com.skillbox.ascent.utils.twoDigit
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class AlertFragment :
    ViewBindingFragment<FragmentAlertSetBinding>(FragmentAlertSetBinding::inflate) {
    private val viewModel: AlertViewModel by viewModels()
    private var onOffAlert by Delegates.notNull<Boolean>() //эта же переменнная меняется при нажатии на кнопку ВКЛ, ОТКЛ -поэтому поздняя иниц
    private var alertInterface: StateAlertInterface? = null
    private var timeAlert = ""
    private var alertHour = 0
    private var alertMin = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //запустим, чтобы взять состояние вьюшек ВКЛ: (по умолчанию true) и время(по умолчанию 19:30)
        viewModel.readStateOnOffTimeAlert()
        bindViewModel()
    }

    private fun bindViewModel() {
        binding.changeAlarmTimeButton.setOnClickListener { setAlertTime() }
        binding.onOffButton.setOnClickListener {
            //MainActivity через интерфейс  слушает что делать с WorkManager(onOffAlert отрицаем,
            // действуем обратно надписи на кнопке вкл,откл)
            if (!onOffAlert) alertInterface?.isStartWM(true) else
                alertInterface?.isStartWM(false)
            //запишем состояние в преференсы
            viewModel.writeOnOffTimeAlert(StateAlert(onOffAlert.not(), timeAlert))
            viewModel.readStateOnOffTimeAlert() //прочитаем,чтобы взять состояние вьешек
        }
        viewModel.stateAlertLiveData.observe(viewLifecycleOwner) { stateAlert ->
            onOffAlert = stateAlert.onOffButtonIsOn
            timeAlert = stateAlert.timeAlert
            binding.timeTextView.text = stateAlert.timeAlert
            binding.onOffButton.text =
                if (!onOffAlert) getString(R.string.enable) else getString(R.string.disable)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { errorMsg ->
            snackBar(binding.alertConstraint, errorMsg)
        }
    }

    private fun setAlertTime() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            alertHour = hour
            alertMin = minute
            timeAlert = ("${twoDigit(alertHour)}:${twoDigit(alertMin)}")
            //отключим WorkManager,запишем выбранное время в преференсы и сбросим состояние кнопки вкл,откл
            alertInterface?.isStartWM(false)
            viewModel.writeOnOffTimeAlert(StateAlert(false, timeAlert))
            viewModel.readStateOnOffTimeAlert()  //прочитаем и обновим состояние вью

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            alertInterface = context as StateAlertInterface
        } catch (e: Exception) {
            e.message?.let { Log.d("msg", it) }
        }
    }

    override fun onResume() {
        super.onResume()
        statusBottomBar(requireActivity(), false)
    }

    companion object {
        const val HAS_VISITED = "hasVisited"
        const val ALERT_TIME_KEY = "alert_time_key"
        const val ON_OFF_ALARM = "on_off_alert"
        const val ON_OFF_IS_ON_KEY = "on_off_is_on_key"
    }
}
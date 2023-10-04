package com.skillbox.ascent.ui.create_activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.skillbox.ascent.R
import com.skillbox.ascent.data.TypeActivityEnum
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.databinding.FragmentCreateActivityBinding
import com.skillbox.ascent.interfaces.PickerInterface
import com.skillbox.ascent.ui.custompicker.DistanceDialogFragment
import com.skillbox.ascent.ui.custompicker.ElapcedDialogFragment
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

@AndroidEntryPoint
class CreateActivityFragment :
    ViewBindingFragment<FragmentCreateActivityBinding>(FragmentCreateActivityBinding::inflate),
    PickerInterface {
    private val viewModel: CreateActivityViewModel by viewModels()
    private lateinit var currentActivity: Activity
    private lateinit var toolbar: Toolbar
    private var dateFromPicker = ""
    private var typeActivity = ""
    private var dateForIso = ""
    private var timeForIso = ""
    private var elapsedTime = 0
    private var distance = 0f
    private var setHour = 0
    private var setMin = 0
    private var language = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        language = Locale.getDefault().language
        toolbar = view.findViewById(R.id.toolBar)
        toolbar.isVisible = true
        toolbar.title = getString(R.string.create_activity)
        val type = mutableListOf<String>()
        if (language == getString(R.string.language_ru)) {
            TypeActivityEnum.values().forEach { type.add(it.lang.second) }
        } else {
            TypeActivityEnum.values().forEach { type.add(it.lang.first) }
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.item_type_activity, type)
        binding.typeEditTextView.setAdapter(adapter)
        bindViewModel()
    }

    private fun bindViewModel() {
        listenerMenuToolBar(toolbar)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.typeEditTextView.setOnClickListener { getTypeActivity() }
        binding.date.setOnClickListener { setDateFromDatePicker() }
        binding.time.setOnClickListener { setTimeFromTimePicker() }
        binding.elapsedTime.setOnClickListener {
            ElapcedDialogFragment().show(childFragmentManager, ELAPSED_PICKER_TAG)
        }
        binding.distance.setOnClickListener {
            DistanceDialogFragment().show(childFragmentManager, DISTANCE_PICKER_TAG)
        }
        binding.createButton.setOnClickListener {
            if (validationForm()) viewModel.createActivity(currentActivity) else
                toast(getString(R.string.fill_fields))
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            snackBarFailed(binding.createScrollView, it, searchBottomView(requireActivity()))
            viewModel.addActivityInDb(currentActivity)  // поместим тренировку в базу
            findNavController().popBackStack()
        }
        viewModel.isCreateLiveData.observe(viewLifecycleOwner) {
            snackBarSuccess(
                binding.createScrollView, getString(R.string.activity_success_add),
                searchBottomView(requireActivity())
            )
            findNavController().popBackStack()
        }
    }

    //--берем дату из DatePicker-------------------------------------------------------------------
    private fun setDateFromDatePicker() {

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTitleText(getString(R.string.select_date_activity))
                .build()
        datePicker.addOnPositiveButtonClickListener {
            val formatterForIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatterTv = SimpleDateFormat("dd MMMM", Locale.getDefault())
            dateForIso = formatterForIso.format(datePicker.selection!!)
            dateFromPicker = formatterTv.format(datePicker.selection!!)

            binding.dateTextField.editText?.setText(dateFromPicker)
        }
        datePicker.show(parentFragmentManager, DATE_PICKER_TAG)
    }

    //--берем время из TimePicker-------------------------------------------------------------------
    private fun setTimeFromTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker =
            MaterialTimePicker.Builder()
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(getString(R.string.select_time_activity))
                .build()
        timePicker.addOnPositiveButtonClickListener {
            setHour = timePicker.hour
            setMin = timePicker.minute

            val timeTV = ("${twoDigit(setHour)}:${twoDigit(setMin)}")
            timeForIso = "$timeTV:00.0"
            binding.timeTextField.editText?.setText(timeTV)
        }
        timePicker.show(parentFragmentManager, TIME_PICKER_TAG)

    }


    //--проверяем форму на валидность---------------------------------------------------------------
    private fun validationForm(): Boolean {
        getTypeActivity()
        val startDateTimeForServer = isoDateTimeFromLocalDateTime()
        val title = binding.name.text?.toString().orEmpty()
        val description = binding.description.text.toString()
        val durationTV = binding.elapsedTime.text?.toString().orEmpty()
        val distanceTV = binding.distance.text?.toString().orEmpty()
        val titleDistanceType = title.isBlank() || distanceTV.isBlank() || typeActivity == ""
        val durationStartDateTime = durationTV.isBlank() || startDateTimeForServer == ""
        val isValid = titleDistanceType || durationStartDateTime
        return if (isValid) {
            false
        } else {
            currentActivity = Activity(
                name = title,
                type = typeActivity,
                startDateLocal = startDateTimeForServer,
                elapsedTime = elapsedTime,
                description = description,
                distance = distance,
                trainer = 1,
                commute = 1
            )
            true
        }
    }


    override fun getValue(key: Int, value: String) {
        if (key == 2) { //по ключю отслеживаем откуда
            val metre = value.substringAfter("/")
            val kilometer = value.substringBefore("/")
            val distanceTV = "$kilometer.$metre ${getString(R.string.km)}"
            distance = kilometer.toFloat() * 1000 + metre.toFloat() * 100
            binding.distanceTextField.editText?.setText(distanceTV)
        }
        if (key == 3) {
            val hour = value.substringBefore("/").toInt()
            val min = value.substringBeforeLast("/").substringAfter("/").toInt()
            val sec = value.substringAfterLast("/").toInt()
            elapsedTime = hour * 3600 + min * 60 + sec
            val elapsedTimeTv = "$hour${getString(R.string.hour_)} $min${getString(R.string.min_)}"
            binding.elapsedTimeTextField.editText?.setText(elapsedTimeTv)
        }
    }


    private fun isoDateTimeFromLocalDateTime(): String {
        var dateTime = ""
        if (dateForIso.isNotEmpty() && timeForIso.isNotEmpty()) {
            val formatter = DateTimeFormatterBuilder() //чтобы перевести строковое
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter()

            val timeMillis = LocalDateTime.parse("$dateForIso $timeForIso", formatter)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            dateTime = isoFormatter.format(timeMillis)
        }
        return dateTime
    }


    private fun getTypeActivity() {
        binding.typeEditTextView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                typeActivity = adapterView.getItemAtPosition(i).toString()
                //если язык русский, то берем тип тренировки из enum на английском
                if (language == getString(R.string.language_ru)) {
                    TypeActivityEnum.values().forEach {
                        if (it.lang.second == typeActivity) typeActivity = it.lang.first
                    }
                }
            }
    }


    override fun onResume() {
        super.onResume()
        statusBottomBar(requireActivity(), true)
    }

    companion object {
        const val TIME_PICKER_TAG = "time_tag"
        const val DATE_PICKER_TAG = "date_tag"
        const val DISTANCE_PICKER_TAG = "distance"
        const val ELAPSED_PICKER_TAG = "elapsed_time"
    }
}

package com.skillbox.ascent.data

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.activity.DetailedActivity
import com.skillbox.ascent.main.MainActivity
import com.skillbox.ascent.main.MainActivity.Companion.ALERT_WORKER_ID
import com.skillbox.ascent.networking.Networking
import com.skillbox.ascent.ui.alert.AlertFragment
import com.skillbox.ascent.utils.calculateDeferredTime
import com.skillbox.ascent.worker.OneTimeWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Repository @Inject constructor(@ApplicationContext private val context: Context) {
    //-----получам информацию о своем профиле-------------------------------------------------------
    private val workManager = WorkManager.getInstance(context)
    private val trackingAddSharedPref by lazy {
        context.getSharedPreferences(MainActivity.TRACKING_ADD_ACTIVITY, Context.MODE_PRIVATE)
    }

    private val stateOnOffTimeAlertSharedPref by lazy {
        context.getSharedPreferences(AlertFragment.ON_OFF_ALARM, Context.MODE_PRIVATE)
    }

    fun getProfileInformation(
        onComplete: (DetailedAthlete) -> Unit,
        onError: (String) -> Unit
    ) {
        Networking.stravaApi.viewProfileInformation().enqueue(
            object : Callback<DetailedAthlete> {
                override fun onResponse(
                    call: Call<DetailedAthlete>,
                    response: Response<DetailedAthlete>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onComplete(it) }
                    } else {
                        onError(response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<DetailedAthlete>, t: Throwable) {//
                    t.message?.let { onError(it) }
                }
            }
        )
    }

    fun loadActivities(
        onComplete: (List<DetailedActivity>) -> Unit,
        onError: (String) -> Unit
    ) {
        val before = LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneOffset.UTC).toEpochSecond()
        Networking.stravaApi.getActivities(1, 30)
            .enqueue(object : Callback<List<DetailedActivity>> {
                override fun onResponse(
                    call: Call<List<DetailedActivity>>,
                    response: Response<List<DetailedActivity>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onComplete(it) }
                    } else {
                        onError(response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<List<DetailedActivity>>, t: Throwable) {
                    t.message?.let { onError(it) }
                }

            })
    }

    //-----создаем тренировку-----------------------------------------------------------------------
    fun createActivity(
        activity: Activity,
        onComplete: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        Networking.stravaApi.createActivity(
            activity.name,
            activity.type,
            activity.startDateLocal,
            activity.elapsedTime,
            activity.description,
            activity.distance,
            activity.trainer,
            activity.commute
        ).enqueue(
            object : Callback<DetailedActivity> {
                override fun onResponse(
                    call: Call<DetailedActivity>,
                    response: Response<DetailedActivity>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onComplete(true) }
                    } else {
                        onError(response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<DetailedActivity>, t: Throwable) {
                    t.message?.let { onError(it) }
                }

            }
        )
    }

    //-----изменяем свой вес------------------------------------------------------------------------
    fun editWeight(
        weight: Float,
        onComplete: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        Networking.stravaApi.editAthleteWeight(weight).enqueue(
            object : Callback<DetailedAthlete> {
                override fun onResponse(
                    call: Call<DetailedAthlete>,
                    response: Response<DetailedAthlete>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { onComplete(it.weight) }
                    } else {
                        onError(response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<DetailedAthlete>, t: Throwable) {
                    t.message?.let { onError(it) }
                }

            }
        )
    }


    //*********************очистка*****************************************************************
    fun clearCache(): Boolean {
        try {
            return deleteFile(context.cacheDir)  //берем папку с кэш
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun deleteFile(file: File?): Boolean {
        file?.let {
            when {
                it.isDirectory -> {
                    val children = it.list()  //
                    for (i in children.indices) {
                        val success = deleteFile(File(file, children[i]))
                        if (!success) return false
                    }
                    return it.delete()
                }
                it.isFile -> return it.delete()
                else -> return false
            }
        }
        return false
    }


    fun writeStateOnOffTimeAlert(stateAlert: StateAlert) {
        with(stateOnOffTimeAlertSharedPref.edit()) {
            putBoolean(AlertFragment.ON_OFF_IS_ON_KEY, stateAlert.onOffButtonIsOn)
            putString(AlertFragment.ALERT_TIME_KEY, stateAlert.timeAlert)
            apply()
        }

    }

    fun readStateOnOffTimeAlert(): StateAlert {
        with(stateOnOffTimeAlertSharedPref) {
            val time = getString(AlertFragment.ALERT_TIME_KEY, "19:30") ?: "19:30"
            val onOff = getBoolean(AlertFragment.ON_OFF_IS_ON_KEY, true)
            return StateAlert(onOff, time)
        }

    }

    private fun deferredTimeWM(time: String): Long {
        val hour = time.substringBefore(":").toInt()
        val min = time.substringAfter(":").toInt()
        return calculateDeferredTime(hour, min)
    }

    fun writeActivitiesAdded(flag: Boolean) {
        trackingAddSharedPref.edit().putBoolean(MainActivity.FLAG_ADDED_KEY, flag).apply()
    }

    fun readStateIsActivitiesAdded(): Boolean {
        return trackingAddSharedPref.getBoolean(MainActivity.FLAG_ADDED_KEY, false)
    }

    //*********************worker*****************************************************************

    fun startOneTimeWork() {
        writeActivitiesAdded(false)   //запишем флаг, что начали наблюдать
        val state = readStateOnOffTimeAlert()
        val deferredTime =
            deferredTimeWM(state.timeAlert)//вычислим время для отложенного из настроек
        val workConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)  //если низкий заряд , то не запускаем задачу
            .build()

        val workRequest = OneTimeWorkRequestBuilder<OneTimeWorker>()
            .setConstraints(workConstraints)
            .setInitialDelay(deferredTime, TimeUnit.MILLISECONDS) //задержка
            .build()
        try {
            workManager.enqueueUniqueWork(
                ALERT_WORKER_ID,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        } catch (t: Throwable) {
            Log.d("msg", "что-то не то с startAlertWorkManager ${t.message}")
        }
    }

    fun cancelWorkManager() {
        workManager.cancelUniqueWork(ALERT_WORKER_ID)

    }

}

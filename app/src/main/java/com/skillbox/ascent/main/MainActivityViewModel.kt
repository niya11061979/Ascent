package com.skillbox.ascent.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.BuildConfig
import com.skillbox.ascent.data.Repository
import com.skillbox.ascent.data.StateAlert
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.auth.AuthRepository
import com.skillbox.ascent.data.db.DbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val dbRepository: DbRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {
    private val stateFirstLaunchMutableLiveData = MutableLiveData<Boolean>()
    private val stateAlertMutableLiveData = MutableLiveData<StateAlert>()
    val stateFirstLaunchApp: LiveData<Boolean>
        get() = stateFirstLaunchMutableLiveData
    val stateAlertLiveData: LiveData<StateAlert>
        get() = stateAlertMutableLiveData

    fun getLazyActivity() {
        viewModelScope.launch {
            try {
                //вытаскиваем все отложенные тренировки из БД
                val lazyList = dbRepository.getLazyActivities()
                val actualLazyList = removeOutDatedLazyActivities(lazyList)
                if (actualLazyList.isNotEmpty()) addLazyActivity(actualLazyList)
            } catch (t: Throwable) {
                Log.d("msg", "error get lazy----${t.message.toString()}")
            }
            BuildConfig.APPLICATION_ID
        }
    }

    //очищаем БД от тренировок не актуальных по времени(дата создания до настоящего )
    // и возращаем новый список
    private fun removeOutDatedLazyActivities(listLazy: List<Activity>): List<Activity> {
        val newList = listLazy as MutableList<Activity>
        newList.forEach { lazyActivity ->
            val currentDateTime = System.currentTimeMillis()
            val dateTimeLazyActivity = Instant.parse(lazyActivity.startDateLocal).toEpochMilli()
            if (currentDateTime > dateTimeLazyActivity) {
                newList.remove(lazyActivity) //удаляем из списка,который будем отправлять в сервер
                removeLazyActivity(lazyActivity.startDateLocal) //удаляем из БД
            }
        }
        return newList  //возвращаем новый список
    }

    private fun addLazyActivity(list: List<Activity>) {
        viewModelScope.launch {
            try {
                list.forEach { lazyActivity ->
                    repository.createActivity(lazyActivity,
                        //если удачно добавили, то удаляем тренировку из БД
                        { isCreate -> if (isCreate) removeLazyActivity(lazyActivity.startDateLocal) },
                        { Log.d("msg", "error create lazy----$it") })
                }

            } catch (t: Throwable) {
                Log.d("msg", "error create lazy----${t.message}")
            }
        }
    }

    private fun removeLazyActivity(startDateLocal: String) {
        viewModelScope.launch {
            try {
                dbRepository.removeLazyActivity(startDateLocal)
            } catch (t: Throwable) {
                Log.d("msg", "error remove lazy----${t.message}")
            }
        }
    }


    fun readStateAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val onOffAlert = repository.readStateOnOffTimeAlert()
                stateAlertMutableLiveData.postValue(onOffAlert)
                if (onOffAlert.onOffButtonIsOn) startWM()
            } catch (_: Throwable) {
            }
        }
    }

    fun startWM() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.startOneTimeWork()
            } catch (_: Throwable) {

            }
        }
    }

    fun cancelWM() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.cancelWorkManager()
            } catch (_: Throwable) {
            }
        }
    }


    fun readStateFirstLaunch() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val state = authRepository.readStateAppFirstLaunch()
                stateFirstLaunchMutableLiveData.postValue(state)
            } catch (t: Throwable) {
                ///  errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun writeFlagFirstLaunchApp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authRepository.stateAppFirstLaunch()
            } catch (t: Throwable) {
                //  errorMutableLiveData.postValue(t.message)
            }
        }
    }


}
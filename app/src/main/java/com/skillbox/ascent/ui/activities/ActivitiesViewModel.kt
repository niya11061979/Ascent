package com.skillbox.ascent.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.Repository
import com.skillbox.ascent.data.activity.Activity
import com.skillbox.ascent.data.activity.DetailedActivity
import com.skillbox.ascent.data.db.DbRepository
import com.skillbox.ascent.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val repository: Repository,
    private val dbRepository: DbRepository
) : ViewModel() {
    private val activitiesMutableLiveData = MutableLiveData<List<DetailedActivity>>()
    private val lazyActivitiesMutableLiveData = MutableLiveData<List<Activity>>()
    private val avatarAthleteMutableLiveData = MutableLiveData<String>()
    private val isLoadingMutableLiveData = SingleLiveEvent<Boolean>()
    private val errorMutableLiveData = MutableLiveData<String>()
    val activitiesLiveData: LiveData<List<DetailedActivity>>
        get() = activitiesMutableLiveData
    val lazyActivitiesLiveData: LiveData<List<Activity>>
        get() = lazyActivitiesMutableLiveData
    val avatarAthleteLiveData: LiveData<String>
        get() = avatarAthleteMutableLiveData
    val isLoadingLiveData: LiveData<Boolean>
        get() = isLoadingMutableLiveData
    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData
    //чтобы не делать запрос в сеть при каждом повороте экрана
    init {
        getAthlete()
        getLazyActivities()
        loadActivities()
    }

    fun loadActivities() {
        viewModelScope.launch {
            try {
                isLoadingMutableLiveData.postValue(true)
                repository.loadActivities(
                    { list ->
                        if (list.isNotEmpty()) clearActivities()
                        activitiesMutableLiveData.postValue(list)
                        saveActivities(list) // сохраняем список в БД

                    },
                    { error -> errorMutableLiveData.postValue(error) }
                )
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            } finally {
                isLoadingMutableLiveData.postValue(false)
            }
        }
    }

    private fun clearActivities() {
        viewModelScope.launch {
            try {
                dbRepository.clearActivitiesDb()
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    private fun saveActivities(list: List<DetailedActivity>) {
        viewModelScope.launch {
            try {
                dbRepository.saveActivities(list)
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun loadActivitiesFromDb() {
        viewModelScope.launch {
            try {
                activitiesMutableLiveData.postValue(dbRepository.loadActivitiesFromDb())
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun getAthlete() {
        viewModelScope.launch {
            try {
                val athlete = dbRepository.loadAthleteFromDb()
                avatarAthleteMutableLiveData.postValue(athlete.avatarUrl)
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }

        }
    }

    fun getLazyActivities() {
        viewModelScope.launch {
            try {
                val lazyList = dbRepository.getLazyActivities()
                lazyActivitiesMutableLiveData.postValue(lazyList)
            } catch (t: Throwable) {

            }
        }
    }
}




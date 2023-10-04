package com.skillbox.ascent.ui.alert

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.Repository
import com.skillbox.ascent.data.StateAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
) : AndroidViewModel(application) {
    private val stateAlertMutableLiveData = MutableLiveData<StateAlert>()
    private val errorMutableLiveData = MutableLiveData<String>()
    val stateAlertLiveData: LiveData<StateAlert>
        get() = stateAlertMutableLiveData
    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData


    fun readStateOnOffTimeAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val onOffAlertTime = repository.readStateOnOffTimeAlert()
                stateAlertMutableLiveData.postValue(onOffAlertTime)
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun writeOnOffTimeAlert(stateAlert: StateAlert) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.writeStateOnOffTimeAlert(stateAlert)
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }
}
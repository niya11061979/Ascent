package com.skillbox.ascent.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.DetailedAthlete
import com.skillbox.ascent.data.Repository
import com.skillbox.ascent.data.auth.AuthRepository
import com.skillbox.ascent.data.db.DbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val authRepository: AuthRepository,
    private val dbRepository: DbRepository
) : AndroidViewModel(application) {

    private val accessTokenSharedMutableLiveData = MutableLiveData<String>()
    private val profileMutableLiveData = MutableLiveData<DetailedAthlete>()
    private val dataClearedMutableLiveData = MutableLiveData<Boolean>()
    private val weightEditMutableLiveData = MutableLiveData<Boolean>()
    private val isLoadingMutableLiveData = MutableLiveData<Boolean>()
    private val errorMutableLiveData = MutableLiveData<String>()
    val profileLiveData: LiveData<DetailedAthlete>
        get() = profileMutableLiveData
    val valueSharedPrefLiveData: LiveData<String>
        get() = accessTokenSharedMutableLiveData

    val weightEditLiveData: LiveData<Boolean>
        get() = weightEditMutableLiveData
    val isLoadingLiveData: LiveData<Boolean>
        get() = isLoadingMutableLiveData
    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData
    val dataCleared: LiveData<Boolean>
        get() = dataClearedMutableLiveData

    //чтобы не делать запрос в сеть при каждом повороте экрана
    init {
        profileInformation()
    }

    private fun profileInformation() {
        viewModelScope.launch {
            isLoadingMutableLiveData.postValue(true)
            try {
                repository.getProfileInformation(
                    onComplete = { profileInformation ->
                        profileMutableLiveData.postValue(profileInformation)
                        insertAthlete(profileInformation)
                    },
                    onError = { error -> errorMutableLiveData.postValue(error) }
                )
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            } finally {
                isLoadingMutableLiveData.postValue(false)
            }
        }
    }

    private fun insertAthlete(profileInformation: DetailedAthlete) {
        try {
            viewModelScope.launch { dbRepository.insertAthlete(profileInformation) }
        } catch (t: Throwable) {
            errorMutableLiveData.postValue(t.message)
        }

    }

    fun deauthorization(accessToken: String) {
        viewModelScope.launch {
            try {
                authRepository.deauthorization(accessToken)
                clearingAppData()  // если успешно, то очишаем данные приложения
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun editWeightAthlete(weight: Float) {
        viewModelScope.launch {

            try {
                repository.editWeight(weight, onComplete = {
                    if (weight.toInt() == it) weightEditMutableLiveData.postValue(true)
                }
                ) { errorMutableLiveData.postValue(it) }
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }


    fun getAccessTokenFromShared() { //
        viewModelScope.launch(Dispatchers.IO) {
            try {
                accessTokenSharedMutableLiveData.postValue(authRepository.getAccessToken())
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }


    private fun clearingAppData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataClearedMutableLiveData.postValue(repository.clearCache())
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

    fun loadAthleteFromDb() {
        viewModelScope.launch {
            try {
                profileMutableLiveData.postValue(dbRepository.loadAthleteFromDb())
            } catch (t: Throwable) {
                errorMutableLiveData.postValue(t.message)
            }
        }
    }

}


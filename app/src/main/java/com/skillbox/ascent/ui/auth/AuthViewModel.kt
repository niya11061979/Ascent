package com.skillbox.ascent.ui.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.R
import com.skillbox.ascent.data.auth.AuthRepository
import com.skillbox.ascent.data.token.AccessToken
import com.skillbox.ascent.data.token.RefreshToken
import com.skillbox.ascent.ui.profile.ProfileFragment.Companion.KEY_ACCESS_TOKEN
import com.skillbox.ascent.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {
    private val authService: AuthorizationService = AuthorizationService(application)
    private val loadingMutableLiveData = MutableLiveData(false)
    private val openAuthPageLiveEvent = SingleLiveEvent<Intent>()
    private val errorMutableLiveData = MutableLiveData<String>()
    private val authSuccessLiveEvent = SingleLiveEvent<Unit>()
    private val toastLiveEvent = SingleLiveEvent<Int>()

    val openAuthPageLiveData: LiveData<Intent>
        get() = openAuthPageLiveEvent
    val authSuccessLiveData: LiveData<Unit>
        get() = authSuccessLiveEvent
    val loadingLiveData: LiveData<Boolean>
        get() = loadingMutableLiveData
    val errorLiveData: LiveData<String>
        get() = errorMutableLiveData

    fun onAuthCodeFailed() {
        toastLiveEvent.postValue(R.string.auth_canceled)
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            loadingMutableLiveData.postValue(true)
            authRepository.performTokenRequest(
                authService = authService,
                tokenRequest = tokenRequest,
                onComplete = {
                    val status =
                        it.accessTokenExpirationTime!! - System.currentTimeMillis() < 3600000
                    authRepository.addAccessToken(
                        KEY_ACCESS_TOKEN,
                        "${it.accessToken}"
                    ) //положим в shared
                    if (status) {
                        RefreshToken.token = it.refreshToken!! // refresh_token беру из ответа
                        refreshToken()             //делаю запрос на обновление токена
                        loadingMutableLiveData.postValue(false)
                        authSuccessLiveEvent.postValue(Unit)
                    } else {
                        AccessToken.token = it.accessToken!! //иначе использую этот токен
                        loadingMutableLiveData.postValue(false)
                        authSuccessLiveEvent.postValue(Unit)
                    }

                },
                onError = {
                    loadingMutableLiveData.postValue(false)
                    toastLiveEvent.postValue(R.string.auth_canceled)
                }
            )
        }

    }

    fun openLoginPage() {
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest()
        )
        openAuthPageLiveEvent.postValue(openAuthPageIntent)
    }

    private fun refreshToken() {
        viewModelScope.launch {
            authRepository.refresh(
                onCompleteRefresh = {
                    AccessToken.token =
                        it.accessToken  //после запроса на обновления для дступа беру новый токен
                    loadingMutableLiveData.postValue(false)
                    authSuccessLiveEvent.postValue(Unit)
                },
                onErrorRefresh = { errorMutableLiveData.postValue(it) }
            )
        }

    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
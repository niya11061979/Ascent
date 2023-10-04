package com.skillbox.ascent.data.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import com.skillbox.ascent.data.token.TokenInformation
import com.skillbox.ascent.networking.Networking
import com.skillbox.ascent.ui.alert.AlertFragment.Companion.HAS_VISITED
import com.skillbox.ascent.ui.profile.ProfileFragment.Companion.APP_SETTING
import com.skillbox.ascent.ui.profile.ProfileFragment.Companion.KEY_ACCESS_TOKEN
import com.skillbox.ascent.ui.profile.ProfileFragment.Companion.SHARED_PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private val accessTokenSharedPref by lazy {
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }
    private val appFirstLaunchSharedPref by lazy {
        context.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE)
    }

    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI)
        )

        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .setPrompt(AuthConfig.PROMPT)
            .build()
    }


    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: (TokenResponse) -> Unit,
        onError: () -> Unit
    ) {
        authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, _ ->
            when {
                response != null -> {
                    Log.d("msg", response.accessTokenExpirationTime.toString())
                    onComplete(response)
                }
                else -> onError()
            }
        }
    }

    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    suspend fun refresh(
        onCompleteRefresh: (TokenInformation) -> Unit,
        onErrorRefresh: (String) -> Unit
    ) {
        Networking.authApi.refreshToken().enqueue(object : Callback<TokenInformation> {
            override fun onResponse(
                call: Call<TokenInformation>,
                response: Response<TokenInformation>
            ) {
                if (response.isSuccessful) {
                    onCompleteRefresh(response.body()!!)
                } else {
                    onErrorRefresh(response.errorBody().toString())

                }
            }

            override fun onFailure(call: Call<TokenInformation>, t: Throwable) {
                t.message?.let { onErrorRefresh(it) }
            }

        })
    }

    suspend fun deauthorization(accessToken: String) {
        Networking.authApi.deauthorization(accessToken)
    }

    //*********************работа с преференсами****************************************************
    fun addAccessToken(key: String, value: String) {
        accessTokenSharedPref.edit()
            .putString(key, value)
            .apply()
    }

    fun readStateAppFirstLaunch(): Boolean {
        return appFirstLaunchSharedPref.getBoolean(HAS_VISITED, false)
    }

    fun stateAppFirstLaunch() {
        appFirstLaunchSharedPref.edit()
            .putBoolean(HAS_VISITED, true)
            .apply()
    }

    fun getAccessToken(): String? {
        return accessTokenSharedPref.getString(KEY_ACCESS_TOKEN, null)
    }
}
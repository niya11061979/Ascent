package com.skillbox.ascent.networking

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.skillbox.ascent.utils.haveQ
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetStateLiveData @Inject constructor(@ApplicationContext private val context: Context) :
    LiveData<Boolean>() {
    private var connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        if (haveQ()) {
            connectivityManager.registerDefaultNetworkCallback(getConnectivityManagerCallback())
        } else {
            networkAvailableRequest()
        }

    }

    override fun onInactive() {
        super.onInactive()
        if (haveQ()) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        }
    }


    private fun networkAvailableRequest() {
        val builder = NetworkRequest.Builder()
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(
            builder.build(),
            getConnectivityManagerCallback()
        )
    }

    private fun getConnectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }

            override fun onLost(network: Network) {
                postValue(false)
            }
        }
        return connectivityManagerCallback
    }
/*
    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateConnection()
        }
    }

    private fun updateConnection() {
        val activeNetwork = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }

 */


}
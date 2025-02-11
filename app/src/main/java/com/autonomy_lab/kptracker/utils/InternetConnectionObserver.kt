package com.autonomy_lab.kptracker.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class InternetConnectionObserver(
    private val context: Context
) {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()


    val isInternetAvailable: Flow<Boolean>
        get() = callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback(){

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(false)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(false)
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(true)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    trySend(connected)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            }
        }

}
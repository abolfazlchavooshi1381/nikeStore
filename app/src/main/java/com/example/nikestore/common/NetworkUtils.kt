package com.example.nikestore.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    private var networkReceiver: BroadcastReceiver? = null
    private var networkChangeListener: NetworkChangeListener? = null

    fun registerNetworkChangeListener(context: Context, listener: NetworkChangeListener) {
        networkChangeListener = listener

        networkReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                networkChangeListener?.onNetworkChanged(isNetworkConnected(context))
            }
        }

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, intentFilter)
    }

    fun unregisterNetworkChangeListener(context: Context) {
        networkChangeListener = null
        context.unregisterReceiver(networkReceiver)
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    interface NetworkChangeListener {
        fun onNetworkChanged(isConnected: Boolean)
    }
}
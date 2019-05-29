package com.worldpay.access.checkout.api

import android.content.Context
import android.support.v4.content.LocalBroadcastManager

internal class LocalBroadcastManagerFactory(private val context: Context) {
    fun createInstance(): LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

}

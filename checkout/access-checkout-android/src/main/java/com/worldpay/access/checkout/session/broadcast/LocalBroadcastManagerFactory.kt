package com.worldpay.access.checkout.session.broadcast

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager

internal class LocalBroadcastManagerFactory(private val context: Context) {

    fun createInstance(): LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

}
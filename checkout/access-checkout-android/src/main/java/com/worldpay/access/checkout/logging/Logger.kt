package com.worldpay.access.checkout.logging

import android.util.Log
import com.worldpay.access.checkout.BuildConfig

interface Logger {

    fun debugLog(tag: String, message: String)

    fun errorLog(tag: String, message: String)
}

class AccessCheckoutLogger : Logger {
    override fun debugLog(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    override fun errorLog(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

}
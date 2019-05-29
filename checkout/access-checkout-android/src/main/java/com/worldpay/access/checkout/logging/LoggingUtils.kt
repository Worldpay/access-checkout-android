package com.worldpay.access.checkout.logging

import android.util.Log
import com.worldpay.access.checkout.BuildConfig

class LoggingUtils {
    companion object {
        @JvmStatic
        fun debugLog(tag: String, msg: String) {
            if (BuildConfig.DEBUG)
                Log.d(tag, msg)
        }

        @JvmStatic
        fun errorLog(tag: String, msg: String) {
            if (BuildConfig.DEBUG)
                Log.e(tag, msg)
        }
    }
}
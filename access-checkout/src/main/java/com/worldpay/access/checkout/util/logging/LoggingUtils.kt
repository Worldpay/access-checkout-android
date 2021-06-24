package com.worldpay.access.checkout.util.logging

/**
 * Static utility methods for logging
 */
internal object LoggingUtils {

    private val accessCheckoutLogger = AccessCheckoutLogger()

    @JvmStatic
    fun debugLog(tag: String, msg: String) {
        accessCheckoutLogger.debugLog(tag, msg)
    }
}

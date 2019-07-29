package com.worldpay.access.checkout.logging

/**
 * Static utility methods for logging
 */
object LoggingUtils {

    private val accessCheckoutLogger = AccessCheckoutLogger()

    @JvmStatic
    fun debugLog(tag: String, msg: String) {
        accessCheckoutLogger.debugLog(tag, msg)
    }

    @JvmStatic
    fun errorLog(tag: String, msg: String) {
        accessCheckoutLogger.errorLog(tag, msg)
    }
}
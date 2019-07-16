package com.worldpay.access.checkout.logging

/**
 * Static utility methods for logging
 */
object LoggingUtils {

    private val accessCheckoutLogger = AccessCheckoutLogger()

    fun debugLog(tag: String, msg: String) {
        accessCheckoutLogger.debugLog(tag, msg)
    }

    fun errorLog(tag: String, msg: String) {
        accessCheckoutLogger.errorLog(tag, msg)
    }
}
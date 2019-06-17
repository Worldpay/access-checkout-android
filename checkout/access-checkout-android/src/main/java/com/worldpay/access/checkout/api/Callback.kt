package com.worldpay.access.checkout.api

/**
 * A callback interface used for returning either an [Exception] or a generic response type [T] to
 * the implementer
 */
interface Callback<T> {

    /**
     * @param error an exception, if available
     * @param response a response, if available
     */
    fun onResponse(error: Exception?, response: T?)
}
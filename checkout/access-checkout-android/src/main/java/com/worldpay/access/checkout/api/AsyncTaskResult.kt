package com.worldpay.access.checkout.api

internal class AsyncTaskResult<T> {

    var result: T? = null
        private set
    var error: Exception? = null
        private set

    constructor(result: T) : super() {
        this.result = result
    }

    constructor(error: Exception) : super() {
        this.error = error
    }
}
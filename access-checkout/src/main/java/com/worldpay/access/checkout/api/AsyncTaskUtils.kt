package com.worldpay.access.checkout.api

internal object AsyncTaskUtils {

    fun <T> callbackOnTaskResult(callback: Callback<T>?, asyncTaskResult: AsyncTaskResult<T>) {
        callback?.let {
            asyncTaskResult.result?.let {
                callback.onResponse(null, it)
            }

            asyncTaskResult.error?.let {
                callback.onResponse(it, null)
            }
        }
    }
}

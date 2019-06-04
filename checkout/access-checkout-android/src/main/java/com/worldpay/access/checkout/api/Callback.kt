package com.worldpay.access.checkout.api

internal interface Callback<T> {

    fun onResponse(error: Exception?, response: T?)
}
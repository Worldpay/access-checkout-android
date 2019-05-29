package com.worldpay.access.checkout.api

interface Callback<T> {

    fun onResponse(error: Exception?, response: T?)
}
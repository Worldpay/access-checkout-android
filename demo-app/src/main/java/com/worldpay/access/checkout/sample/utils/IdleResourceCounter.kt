package com.worldpay.access.checkout.sample.utils

interface IdleResourceCounter {
    fun increment()
    fun decrement()
    fun unregisterIdleResCounter()
}

object IdleResourceCounterFactory {
    fun getResCounter(name: String): IdleResourceCounter = CustomIdlingResourceCounter(name)
}

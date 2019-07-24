package com.worldpay.access.checkout.utils

import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource

class CustomIdlingResourceCounter(idlingResName: String) {
    private val counter: CountingIdlingResource

    init {
        counter = CountingIdlingResource(idlingResName)
        IdlingRegistry.getInstance().register(counter)
    }

    fun increment() = counter.increment()

    fun decrement() = counter.decrement()

    fun unregisterIdleResCounter() = IdlingRegistry.getInstance().unregister(counter)
}



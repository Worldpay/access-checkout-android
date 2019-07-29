package com.worldpay.access.checkout.utils

import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.idling.CountingIdlingResource

class CustomIdlingResourceCounter(idlingResName: String) : IdleResourceCounter {
    private val counter: CountingIdlingResource

    init {
        counter = CountingIdlingResource(idlingResName)
        IdlingRegistry.getInstance().register(counter)
    }

    override fun increment() = counter.increment()

    override fun decrement() = counter.decrement()

    override fun unregisterIdleResCounter() {
        IdlingRegistry.getInstance().unregister(counter)
    }
}

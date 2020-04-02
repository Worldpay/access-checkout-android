package com.worldpay.access.checkout.utils

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource

class CustomIdlingResourceCounter(idlingResName: String) : IdleResourceCounter {

    private val counter: CountingIdlingResource = CountingIdlingResource(idlingResName)

    init {
        IdlingRegistry.getInstance().register(counter)
    }

    override fun increment() = counter.increment()

    override fun decrement() = counter.decrement()

    override fun unregisterIdleResCounter() {
        IdlingRegistry.getInstance().unregister(counter)
    }

}

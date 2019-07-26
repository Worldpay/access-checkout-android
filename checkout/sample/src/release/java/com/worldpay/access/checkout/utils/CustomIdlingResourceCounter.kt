package com.worldpay.access.checkout.utils

//empty class
class CustomIdlingResourceCounter(idlingResName: String): IdleResourceCounter {
    override fun increment() {}
    override fun decrement() {}
    override fun unregisterIdleResCounter() {}
}



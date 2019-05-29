package com.worldpay.access.checkout.testutils

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

fun <T> typeSafeEq(obj: T): T {
    Mockito.eq(obj)
    return uninitialized()
}

fun <T> typeSafeAny(): T {
    Mockito.any<T>()
    return uninitialized()
}

inline fun <reified T: Any> mock(): T = Mockito.mock(T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T

inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
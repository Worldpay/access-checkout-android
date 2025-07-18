package com.worldpay.access.checkout.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface IDispatchersProvider {
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val immediate: CoroutineDispatcher
    val io: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

class CoroutineDispatchers : IDispatchersProvider {
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val immediate: CoroutineDispatcher = Dispatchers.Main.immediate
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

internal object DispatchersProvider {
    var instance: IDispatchersProvider = CoroutineDispatchers()
}
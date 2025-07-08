package com.worldpay.access.checkout.client.testutil

import com.worldpay.access.checkout.util.coroutine.CoroutineDispatchers
import com.worldpay.access.checkout.util.coroutine.DispatchersProvider
import com.worldpay.access.checkout.util.coroutine.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class TestDispatchersProvider(
    testDispatcher: CoroutineDispatcher
) : IDispatchersProvider {
    override val default = testDispatcher
    override val main = testDispatcher
    override val immediate = testDispatcher
    override val io = testDispatcher
    override val unconfined = testDispatcher
}


@OptIn(ExperimentalCoroutinesApi::class)
class AbstractBaseCoroutineTest {
    companion object {
        val testScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)


        fun coroutineSetup() {
            val testDispatchersProvider = TestDispatchersProvider(testDispatcher)
            DispatchersProvider.instance = testDispatchersProvider;
            Dispatchers.setMain(testDispatcher)
        }

        fun coroutineTearDown() {
            testScope.cancel()
            Dispatchers.resetMain()
            DispatchersProvider.instance = CoroutineDispatchers()
        }
    }
}

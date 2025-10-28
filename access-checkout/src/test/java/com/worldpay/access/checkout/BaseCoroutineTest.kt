package com.worldpay.access.checkout

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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations


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
open class BaseCoroutineTest {
    private lateinit var closeable: AutoCloseable
    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    @Before
    fun baseCoroutineSetUp() {
        closeable = MockitoAnnotations.openMocks(this)

        coroutineSetup()
    }

    @After
    fun baseCoroutineTearDown() {
        coroutineTearDown()
    }

    private fun coroutineSetup() {
        // We require to set a test dispatcher before setting our TestDispatchersProvider
        // as main dispatcher - this is following to a kotlinx coroutines upgrade
        // see https://github.com/Kotlin/kotlinx.coroutines/issues/4397
        Dispatchers.setMain(UnconfinedTestDispatcher())

        val testDispatchersProvider = TestDispatchersProvider(testDispatcher)
        DispatchersProvider.instance = testDispatchersProvider;
        Dispatchers.setMain(testDispatcher)
    }

    private fun coroutineTearDown() {
        testScope.cancel()
        testDispatcher.cancel()
        Dispatchers.resetMain()

        // We require to set a test dispatcher before setting our TestDispatchersProvider
        // as main dispatcher - this is following to a kotlinx coroutines upgrade
        // see https://github.com/Kotlin/kotlinx.coroutines/issues/4397
        Dispatchers.setMain(UnconfinedTestDispatcher())

        DispatchersProvider.instance = CoroutineDispatchers()
    }
}

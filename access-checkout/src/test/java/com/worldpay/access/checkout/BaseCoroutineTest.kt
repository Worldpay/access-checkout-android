package com.worldpay.access.checkout

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations


@OptIn(ExperimentalCoroutinesApi::class)
open class BaseCoroutineTest {

    protected val testScheduler = TestCoroutineScheduler()
    protected val testDispatcher = StandardTestDispatcher(testScheduler)
    protected val testScope = TestScope(testDispatcher)

    private lateinit var closeable: AutoCloseable


    @Before
    open fun setupBase() {
        closeable = MockitoAnnotations.openMocks(this)

        //Redirect Dispatchers.Main to testDispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun tearDownBase() {
        Dispatchers.resetMain()
        testScope.cancel()
        closeable.close()
    }
}

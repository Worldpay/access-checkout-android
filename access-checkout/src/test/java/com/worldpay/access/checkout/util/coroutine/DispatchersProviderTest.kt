package com.worldpay.access.checkout.util.coroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DispatchersProviderTest {

    @Before
    fun setUp() {
        // We require to set a test dispatcher before setting our TestDispatchersProvider
        // as main dispatcher - this is following to a kotlinx coroutines upgrade
        // see https://github.com/Kotlin/kotlinx.coroutines/issues/4397
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return default dispatchers from CoroutineDispatchers`() {
        val dispatchers = CoroutineDispatchers()

        assertEquals(Dispatchers.Default, dispatchers.default)
        assertEquals(Dispatchers.Main, dispatchers.main)
        assertEquals(Dispatchers.Main.immediate, dispatchers.immediate)
        assertEquals(Dispatchers.IO, dispatchers.io)
        assertEquals(Dispatchers.Unconfined, dispatchers.unconfined)
    }

    @Test
    fun `should allow updating and resetting DispatchersProvider instance`() {
        val testDispatchers = object : IDispatchersProvider {
            override val default = Dispatchers.Unconfined
            override val main = Dispatchers.Unconfined
            override val immediate = Dispatchers.Unconfined
            override val io = Dispatchers.Unconfined
            override val unconfined = Dispatchers.Unconfined
        }

        // Update the instance
        DispatchersProvider.instance = testDispatchers
        assertEquals(Dispatchers.Unconfined, DispatchersProvider.instance.default)
        assertEquals(Dispatchers.Unconfined, DispatchersProvider.instance.main)

        // Reset the instance
        DispatchersProvider.instance = CoroutineDispatchers()
        assertEquals(Dispatchers.Default, DispatchersProvider.instance.default)
        assertEquals(Dispatchers.Main, DispatchersProvider.instance.main)
    }
}
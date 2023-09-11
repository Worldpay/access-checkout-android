package com.worldpay.access.checkout.session

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ActivityLifecycleObserverTest {

    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val tag = "some tag"
    private val sessionBroadcastManagerFactory = mock(SessionBroadcastManagerFactory::class.java)
    private val sessionBroadcastManager = mock(SessionBroadcastManager::class.java)

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
    }

    private fun createActivityLifecycleObserver() = ActivityLifecycleObserver(
        tag, lifecycleOwner, sessionBroadcastManagerFactory, sessionBroadcastManager
    )

    @Test
    fun `should add as lifecycle observer by posting on handler when not initialised on main thread`() {
        val mainHandlerMock = mockHandler()
        // We are not in an instrumented test so Looper does not actually work
        // Instead we use a shadow of the Looper class which is defined in the android.os package in the test directory
        // We use a method in our custom implementation in order to reset both myLooper and Main Looper
        val resetAnyLooperToNull = Looper::class.java.getMethod("resetAnyLooperToNull")
        resetAnyLooperToNull.invoke(null)
        // This call creates a mock Looper as Main Looper, hence it will be different from myLooper
        Looper.prepareMainLooper()

        val activityLifeCycleObserver = ActivityLifecycleObserver(
            tag, lifecycleOwner, sessionBroadcastManagerFactory, sessionBroadcastManager,
            mainHandlerMock
        )

        verify(lifecycle).addObserver(activityLifeCycleObserver)
        verify(mainHandlerMock).post(any())
    }

    @Test
    fun `should add as lifecycle observer without use of handler when initialised on main thread`() {
        val mainHandlerMock = mockHandler()
        // We are not in an instrumented test so Looper does not actually work
        // Instead we use a shadow of the Looper class which is defined in the android.os package in the test directory
        // We use a method in our custom implementation in order to reset both myLooper and Main Looper
        val resetAnyLooperToNull = Looper::class.java.getMethod("resetAnyLooperToNull")
        resetAnyLooperToNull.invoke(null)

        val activityLifeCycleObserver = ActivityLifecycleObserver(
            tag, lifecycleOwner, sessionBroadcastManagerFactory, sessionBroadcastManager,
            mainHandlerMock
        )

        verify(lifecycle).addObserver(activityLifeCycleObserver)
        verifyNoInteractions(mainHandlerMock)
    }

    @Test
    fun `should register broadcast receivers when activity lifecycle handler has been triggered on start`() {
        val activityLifeCycleObserver = createActivityLifecycleObserver()
        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.onStart()

        verify(sessionBroadcastManager).register()
    }

    @Test
    fun `should unregister broadcast receivers when activity lifecycle handler has been triggered on stop`() {
        val activityLifeCycleObserver = createActivityLifecycleObserver()
        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.onStop()

        verify(sessionBroadcastManager).unregister()
    }

    @Test
    fun `should be able to set lifecycle state flag`() {
        assertFalse(inLifeCycleState)

        inLifeCycleState = true

        assertTrue(inLifeCycleState)

        inLifeCycleState = false
    }

    @Test
    fun `should switch lifecycle state flag to true on pause and false on resume`() {
        val activityLifeCycleObserver = createActivityLifecycleObserver()
        assertFalse(inLifeCycleState)

        activityLifeCycleObserver.onPause()
        assertTrue(inLifeCycleState)

        activityLifeCycleObserver.onResume()
        assertFalse(inLifeCycleState)
    }

    private fun mockHandler(): Handler {
        val handler = mock(Handler::class.java)

        `when`(handler.post(any())).then { invocation ->
            invocation.getArgument<Runnable>(0).run()
            true
        }

        return handler
    }
}

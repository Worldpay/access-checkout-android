package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.common.base.CaseFormat
import com.worldpay.access.checkout.session.ActivityLifecycleObserver.Companion.inLifeCycleState
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock
import org.mockito.internal.util.reflection.FieldSetter
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActivityLifecycleObserverTest {

    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val tag = "some tag"
    private val sessionBroadcastManagerFactory = mock(SessionBroadcastManagerFactory::class.java)

    private lateinit var activityLifeCycleObserver: ActivityLifecycleObserver

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        activityLifeCycleObserver =
            ActivityLifecycleObserver(
                tag,
                lifecycleOwner,
                sessionBroadcastManagerFactory
            )
    }

    @Test
    fun `should register broadcast receivers when activity lifecycle handler has been triggered on start`() {
        val sessionBroadcastManager = mock(SessionBroadcastManager::class.java)
        setMock(sessionBroadcastManager)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.startListener()

        verify(sessionBroadcastManager).register()
    }

    @Test
    fun `should unregister broadcast receivers when activity lifecycle handler has been triggered on stop`() {
        val sessionBroadcastManager = mock(SessionBroadcastManager::class.java)
        setMock(sessionBroadcastManager)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.stopListener()

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
        assertFalse(inLifeCycleState)

        activityLifeCycleObserver.onPause()
        assertTrue(inLifeCycleState)

        activityLifeCycleObserver.onResume()
        assertFalse(inLifeCycleState)
    }

    private fun setMock(clazz: Any) {
        FieldSetter.setField(
            activityLifeCycleObserver,
            activityLifeCycleObserver.javaClass.getDeclaredField(
                CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_CAMEL,
                    clazz::class.simpleName!!
                )
            ),
            clazz
        )
    }

}

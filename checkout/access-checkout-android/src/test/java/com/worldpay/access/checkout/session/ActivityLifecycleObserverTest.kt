package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.common.base.CaseFormat
import com.worldpay.access.checkout.session.request.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.request.broadcast.SessionBroadcastManagerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.mockito.internal.util.reflection.FieldSetter

class ActivityLifecycleObserverTest {

    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val tag = "some tag"
    private val sessionBroadcastManagerFactory : SessionBroadcastManagerFactory = mock(
        SessionBroadcastManagerFactory::class.java)
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
    fun `given activity lifecycle handler has been triggered on resume, then broadcast manager has session request receiver registered`() {
        val sessionBroadcastManager = Mockito.mock(SessionBroadcastManager::class.java)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.resumeListener()

        verify(sessionBroadcastManager).register()
    }

    @Test
    fun `given activity lifecycle handler has been triggered on stop, then broadcast manager has session request receiver unregistered`() {
        val sessionBroadcastManager = Mockito.mock(SessionBroadcastManager::class.java)
        setMock(sessionBroadcastManager)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.stopListener()

        verify(sessionBroadcastManager).unregister()
    }

    @Test
    fun `given activity lifecycle handler has been triggered on stop, then activity lifecycle handler is removed as an observer of the activity lifecycle`() {
        activityLifeCycleObserver.onStopListener()

        verify(lifecycle).removeObserver(activityLifeCycleObserver)
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
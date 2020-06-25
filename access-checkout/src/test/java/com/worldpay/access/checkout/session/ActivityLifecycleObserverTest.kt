package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.common.base.CaseFormat
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManager
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mockito.mock
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
    fun `given activity lifecycle handler has been triggered on start, then broadcast receivers are registered`() {
        val sessionBroadcastManager = mock(SessionBroadcastManager::class.java)
        setMock(sessionBroadcastManager)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.startListener()

        verify(sessionBroadcastManager).register()
    }

    @Test
    fun `given activity lifecycle handler has been triggered on stop, then broadcast receivers are unregistered`() {
        val sessionBroadcastManager = mock(SessionBroadcastManager::class.java)
        setMock(sessionBroadcastManager)

        given(sessionBroadcastManagerFactory.createInstance()).willReturn(sessionBroadcastManager)

        activityLifeCycleObserver.stopListener()

        verify(sessionBroadcastManager).unregister()
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
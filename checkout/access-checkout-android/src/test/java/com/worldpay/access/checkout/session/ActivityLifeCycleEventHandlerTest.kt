package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.common.base.CaseFormat
import com.nhaarman.mockitokotlin2.any
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.session.SessionReceiver
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*
import org.mockito.internal.util.reflection.FieldSetter

class ActivityLifeCycleEventHandlerTest {

    private val lifecycleOwner = mock(LifecycleOwner::class.java)
    private val lifecycle = mock(Lifecycle::class.java)
    private val tag = "some tag"
    private val sessionReceiver: SessionReceiver = mock(SessionReceiver::class.java)
    private val localBroadcastManagerFactory :LocalBroadcastManagerFactory = mock(LocalBroadcastManagerFactory::class.java)
    private lateinit var activityLifeCycleEventHandler: ActivityLifecycleEventHandler

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        activityLifeCycleEventHandler =
            ActivityLifecycleEventHandler(
                tag,
                sessionReceiver,
                lifecycleOwner,
                localBroadcastManagerFactory
            )
    }

    @Test
    fun `given activity lifecycle handler has been triggered on resume, then broadcast manager has session request receiver registered`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        val localBroadcastManagerFactory = mock(LocalBroadcastManagerFactory::class.java)
        setMock(localBroadcastManager)
        setMock(localBroadcastManagerFactory)
        given(localBroadcastManagerFactory.createInstance()).willReturn(localBroadcastManager)

        activityLifeCycleEventHandler.startListener()

        verify(localBroadcastManager).registerReceiver(any(), any())
    }

    @Test
    fun `given activity lifecycle handler has been triggered on stop, then broadcast manager has session request receiver unregistered`() {
        val localBroadcastManager = mock(LocalBroadcastManager::class.java)
        setMock(localBroadcastManager)

        activityLifeCycleEventHandler.disconnectListener()

        verify(localBroadcastManager).unregisterReceiver(any())
    }

    @Test
    fun `given activity lifecycle handler has been triggered on stop, then activity lifecycle handler is removed as an observer of the activity lifecycle`() {
        activityLifeCycleEventHandler.removeObserver()

        verify(lifecycle).removeObserver(activityLifeCycleEventHandler)
    }

    private fun setMock(clazz: Any) {
        FieldSetter.setField(
            activityLifeCycleEventHandler,
            activityLifeCycleEventHandler.javaClass.getDeclaredField(
                CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_CAMEL,
                    clazz::class.simpleName!!
                )
            ),
            clazz
        )
    }

}
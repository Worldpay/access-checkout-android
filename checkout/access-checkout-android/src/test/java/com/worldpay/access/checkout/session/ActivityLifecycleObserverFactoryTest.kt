package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.given
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.session.SessionReceiver
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ActivityLifecycleObserverFactoryTest {


    @Test
    fun `should return an instance of ActivityLifeCycleEventHandler when getHandler is called`() {
        val lifecycle = mock(Lifecycle::class.java)
        val lifecycleOwner = mock(LifecycleOwner::class.java)
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        val factory = ActivityLifecycleObserverInitialiser(
            "tag",
            mock(SessionReceiver::class.java),
            lifecycleOwner,
            mock(LocalBroadcastManagerFactory::class.java)
        )

        val handler = factory.initialise()

        assertNotNull(handler)
        assertTrue(handler is ActivityLifecycleObserver)
    }
}

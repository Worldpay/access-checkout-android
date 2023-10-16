package com.worldpay.access.checkout.session

import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock

class ActivityLifecycleObserverInitialiserTest {

    @Test
    fun `should return an activity lifecycle observer initialiser when initialise is called`() {
        val lifecycleOwner = mock<LifecycleOwner>()
        val sessionBroadcastManagerFactory = mock<SessionBroadcastManagerFactory>()

        given(lifecycleOwner.lifecycle).willReturn(mock())
        given(sessionBroadcastManagerFactory.createInstance()).willReturn(mock())

        val activityLifecycleObserverInitialiser = ActivityLifecycleObserverInitialiser(
            "tag",
            lifecycleOwner,
            sessionBroadcastManagerFactory
        )

        val observer = activityLifecycleObserverInitialiser.initialise()

        assertNotNull(observer)
    }
}

package com.worldpay.access.checkout.session

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.given
import com.worldpay.access.checkout.session.request.broadcast.SessionBroadcastManagerFactory
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

class ActivityLifecycleObserverInitialiserTest {

    @Test
    fun `should return an activity lifecycle observer initialiser when initialise is called`() {
        val lifecycleOwner = mock(LifecycleOwner::class.java)

        given(lifecycleOwner.lifecycle).willReturn(mock(Lifecycle::class.java))

        val activityLifecycleObserverInitialiser = ActivityLifecycleObserverInitialiser(
            "tag",
            lifecycleOwner,
            mock(SessionBroadcastManagerFactory::class.java)
        )

        val observer = activityLifecycleObserverInitialiser.initialise()

        assertNotNull(observer)
    }

}
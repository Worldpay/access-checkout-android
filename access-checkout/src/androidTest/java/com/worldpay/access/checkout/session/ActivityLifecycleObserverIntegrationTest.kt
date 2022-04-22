package com.worldpay.access.checkout.session

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.platform.app.InstrumentationRegistry
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito

class ActivityLifecycleObserverIntegrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().context

    private val tag = "some-tag"
    private val sessionBroadcastManagerFactory = createSessionBroadcastManagerFactory(context)
    private val lifecycleOwner: LifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
    private var lifecycleRegistry = LifecycleRegistry(lifecycleOwner)

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycleRegistry)
    }

    @Test
    fun shouldNotThrowExceptionWhenNotInitialisedOnMainThread() {
        ActivityLifecycleObserver(tag, lifecycleOwner, sessionBroadcastManagerFactory)
    }

    @Test
    fun shouldNotThrowExceptionWhenInitialisedOnMainThread() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            ActivityLifecycleObserver(tag, lifecycleOwner, sessionBroadcastManagerFactory)
        }
    }

    private fun createSessionBroadcastManagerFactory(context: Context): SessionBroadcastManagerFactory {
        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context)

        val sessionResponseListener = object : SessionResponseListener {
            override fun onSuccess(sessionResponseMap: Map<SessionType, String>) {}
            override fun onError(error: AccessCheckoutException) {}
        }

        return SessionBroadcastManagerFactory(
            localBroadcastManagerFactory,
            sessionResponseListener
        )
    }
}
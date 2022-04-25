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
import org.awaitility.Duration.ONE_SECOND
import org.awaitility.kotlin.await
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class ActivityLifecycleObserverIntegrationTest {
    private val context = InstrumentationRegistry.getInstrumentation().context

    private val tag = "some-tag"
    private val sessionBroadcastManagerFactory = createSessionBroadcastManagerFactory(context)
    private val lifecycleOwner: LifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
    private var lifecycleRegistry = spy(LifecycleRegistry(lifecycleOwner))

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycleRegistry)
    }

    @Test
    fun shouldNotThrowExceptionWhenNotInitialisedOnMainThread() {
        val observer =
            ActivityLifecycleObserver(tag, lifecycleOwner, sessionBroadcastManagerFactory)

        await.atMost(ONE_SECOND).until {
            verify(lifecycleRegistry).addObserver(observer)
            true
        }
    }

    @Test
    fun shouldNotThrowExceptionWhenInitialisedOnMainThread() {
        var observer: ActivityLifecycleObserver? = null

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            observer =
                ActivityLifecycleObserver(tag, lifecycleOwner, sessionBroadcastManagerFactory)
        }

        await.atMost(ONE_SECOND).until {
            verify(lifecycleRegistry).addObserver(observer!!)
            true
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
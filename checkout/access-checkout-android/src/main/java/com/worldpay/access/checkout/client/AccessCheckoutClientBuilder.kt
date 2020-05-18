package com.worldpay.access.checkout.client

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.session.AccessCheckoutClientImpl
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerConfig
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerFactory
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * A builder that returns an [AccessCheckoutClientImpl]
 */
class AccessCheckoutClientBuilder {

    private val tag = "AccessCheckoutClient"
    private var baseUrl: String? = null
    private var merchantId: String? = null
    private var context: Context? = null
    private var externalSessionResponseListener: SessionResponseListener? = null
    private var lifecycleOwner: LifecycleOwner? = null

    fun baseUrl(baseURL: String): AccessCheckoutClientBuilder {
        this.baseUrl = baseURL
        return this
    }

    fun merchantId(merchantID: String): AccessCheckoutClientBuilder {
        this.merchantId = merchantID
        return this
    }

    fun context(context: Context): AccessCheckoutClientBuilder {
        this.context = context
        return this
    }

    fun sessionResponseListener(sessionResponseListener: SessionResponseListener): AccessCheckoutClientBuilder {
        this.externalSessionResponseListener = sessionResponseListener
        return this
    }

    fun lifecycleOwner(lifecycleOwner: LifecycleOwner): AccessCheckoutClientBuilder {
        this.lifecycleOwner = lifecycleOwner
        return this
    }

    fun build(): AccessCheckoutClient {
        validateNotNull(baseUrl, "base url")
        validateNotNull(merchantId, "merchant ID")
        validateNotNull(context, "context")
        validateNotNull(externalSessionResponseListener, "session response listener")
        validateNotNull(lifecycleOwner, "lifecycle owner")

        val tokenRequestHandlerConfig = SessionRequestHandlerConfig.Builder()
            .baseUrl(baseUrl as String)
            .merchantId(merchantId as String)
            .context(context as Context)
            .externalSessionResponseListener(externalSessionResponseListener as SessionResponseListener)
            .build()

        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context as Context)

        val activityLifecycleObserverInitialiser = createActivityLifecycleObserverInitialiser(
            localBroadcastManagerFactory,
            externalSessionResponseListener as SessionResponseListener
        )

        return AccessCheckoutClientImpl(
            SessionRequestHandlerFactory(tokenRequestHandlerConfig),
            activityLifecycleObserverInitialiser,
            localBroadcastManagerFactory,
            context as Context
        )
    }

    private fun createActivityLifecycleObserverInitialiser(
        localBroadcastManagerFactory: LocalBroadcastManagerFactory,
        externalSessionResponseListener: SessionResponseListener
    ): ActivityLifecycleObserverInitialiser {
        return ActivityLifecycleObserverInitialiser(
            tag,
            lifecycleOwner as LifecycleOwner,
            SessionBroadcastManagerFactory(localBroadcastManagerFactory, externalSessionResponseListener)
        )
    }

}
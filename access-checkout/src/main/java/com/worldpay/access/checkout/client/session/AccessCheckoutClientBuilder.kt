package com.worldpay.access.checkout.client.session

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.session.AccessCheckoutClientImpl
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerConfig
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerFactory
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * A builder that returns an [AccessCheckoutClient] for the client to use for session generation
 *
 * @see AccessCheckoutClient for more information on how to use the client
 */
class AccessCheckoutClientBuilder {

    private var baseUrl: String? = null
    private var merchantId: String? = null
    private var context: Context? = null
    private var externalSessionResponseListener: SessionResponseListener? = null
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * Sets the base url for Access Worldpay services
     *
     * @param[baseUrl] [String] that represents the base url
     */
    fun baseUrl(baseUrl: String): AccessCheckoutClientBuilder {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * Sets the merchant id of the client
     *
     * @param[merchantId] [String] that represents the id of the merchant given to the client at time of registration
     */
    fun merchantId(merchantId: String): AccessCheckoutClientBuilder {
        this.merchantId = merchantId
        return this
    }

    /**
     * Sets the application context
     *
     * @param[context] [Context] that represents the application
     */
    fun context(context: Context): AccessCheckoutClientBuilder {
        this.context = context
        return this
    }

    /**
     * Sets the [SessionResponseListener] for the client
     *
     * @param[sessionResponseListener] An external session response listener that is notified on http requests
     *
     * @see SessionResponseListener
     */
    fun sessionResponseListener(sessionResponseListener: SessionResponseListener): AccessCheckoutClientBuilder {
        this.externalSessionResponseListener = sessionResponseListener
        return this
    }

    /**
     * Sets the application lifecycle owner
     *
     * @param[lifecycleOwner] [LifecycleOwner] that is required so that broadcast listeners can be registered internally by the SDK
     *
     * @see LifecycleOwner
     */
    fun lifecycleOwner(lifecycleOwner: LifecycleOwner): AccessCheckoutClientBuilder {
        this.lifecycleOwner = lifecycleOwner
        return this
    }

    /**
     * Builds the [AccessCheckoutClient] instance
     *
     * @return [AccessCheckoutClient] interface with a default internal implementation
     * @throws [IllegalArgumentException] is thrown when a property is missing
     */
    fun build(): AccessCheckoutClient {
        validateNotNull(baseUrl, "base url")
        validateNotNull(merchantId, "merchant id")
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
            javaClass.simpleName,
            lifecycleOwner as LifecycleOwner,
            SessionBroadcastManagerFactory(localBroadcastManagerFactory, externalSessionResponseListener)
        )
    }

}

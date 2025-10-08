package com.worldpay.access.checkout.client

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.discovery.ApiDiscoveryClient
import com.worldpay.access.checkout.client.session.BaseUrlSanitiser.sanitise
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.SessionBroadcastManagerFactory
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerConfig
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerFactory
import com.worldpay.access.checkout.util.PropertyValidationUtil
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * A builder that returns an [AccessCheckoutClient] for the client to use for session generation
 *
 * @see AccessCheckoutClient for more information on how to use the client
 */
class AccessCheckoutClientBuilder {

    private var baseUrl: String? = null
    private var checkoutId: String? = null
    private var context: Context? = null
    private var externalSessionResponseListener: SessionResponseListener? = null
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * Sets the base url for Access Worldpay services
     *
     * @param[baseUrl] [String] that represents the base url
     */
    fun baseUrl(baseUrl: String): AccessCheckoutClientBuilder {
        this.baseUrl = sanitise(baseUrl)
        return this
    }

    /**
     * Sets the checkout id of the client
     *
     * @param[checkoutId] [String] that represents the checkoutId given to the merchant at time of registration
     */
    fun checkoutId(checkoutId: String): AccessCheckoutClientBuilder {
        this.checkoutId = checkoutId
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
     * @throws [AccessCheckoutException] is thrown when a property is missing
     */
    fun build(): AccessCheckoutClient {
        validateNotNull(baseUrl, "base url")
        validateNotNull(checkoutId, "checkout id")
        validateNotNull(context, "context")
        validateNotNull(externalSessionResponseListener, "session response listener")
        validateNotNull(lifecycleOwner, "lifecycle owner")

        ApiDiscoveryClient.initialise(baseUrl!!)

        val sessionRequestHandlerConfig = SessionRequestHandlerConfig.Builder()
            .checkoutId(checkoutId!!)
            .context(context!!)
            .build()

        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context!!)

        val activityLifecycleObserverInitialiser = createActivityLifecycleObserverInitialiser(
            localBroadcastManagerFactory,
            externalSessionResponseListener!!
        )

        val accessCheckoutClientDisposer = AccessCheckoutClientDisposer()

        return AccessCheckoutClientImpl(
            SessionRequestHandlerFactory(sessionRequestHandlerConfig),
            activityLifecycleObserverInitialiser,
            localBroadcastManagerFactory,
            context!!,
            checkoutId!!,
            baseUrl!!,
            accessCheckoutClientDisposer
        )
    }

    private fun createActivityLifecycleObserverInitialiser(
        localBroadcastManagerFactory: LocalBroadcastManagerFactory,
        externalSessionResponseListener: SessionResponseListener
    ): ActivityLifecycleObserverInitialiser {
        return ActivityLifecycleObserverInitialiser(
            javaClass.simpleName,
            lifecycleOwner!!,
            SessionBroadcastManagerFactory(
                localBroadcastManagerFactory,
                externalSessionResponseListener
            )
        )
    }
}

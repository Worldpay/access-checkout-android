package com.worldpay.access.checkout.session

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.SessionReceiver
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.session.request.SessionRequestHandlerFactory
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClientImpl] is responsible for handling the request for a session state from the Access Worldpay services.
 */
internal class AccessCheckoutClientImpl(
    baseUrl: String,
    context: Context,
    externalSessionResponseListener: SessionResponseListener,
    lifecycleOwner: LifecycleOwner,
    private val sessionHandlerFactory: SessionRequestHandlerFactory
) : AccessCheckoutClient {

    private val tag = "AccessCheckoutClient"

    init {
        val checkoutSessionResponseListener =
            CheckoutSessionResponseListener(tag, externalSessionResponseListener)
        val sessionReceiver = SessionReceiver(checkoutSessionResponseListener)
        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context)

        debugLog(tag, "Making request to discover endpoint")

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClientFactory.getClient()

        accessCheckoutDiscoveryClient.discover(
            baseUrl = baseUrl,
            discoverLinks = DiscoverLinks.verifiedTokens
        )

        ActivityLifecycleEventHandler(
            tag,
            sessionReceiver,
            lifecycleOwner,
            localBroadcastManagerFactory
        )
    }

    /**
     * Method which triggers a generate session state request to the Access Worldpay sessions API. The response will come back through the
     * [SessionResponseListener]
     *
     * @param cardDetails the cardDetails to submit - see [CardDetailsBuilder]
     * @param sessionTypes the list of tokens that is being requested
     */
    override fun generateSession(cardDetails: CardDetails, sessionTypes: List<SessionType>) {
        val handlers = sessionHandlerFactory.getTokenHandlers()

        for (handler in handlers) {
            if (handler.canHandle(sessionTypes)) {
                handler.handle(cardDetails)
            }
        }
    }

}


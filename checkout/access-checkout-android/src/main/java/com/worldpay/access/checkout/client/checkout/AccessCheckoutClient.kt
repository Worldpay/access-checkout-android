package com.worldpay.access.checkout.client.checkout

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.SessionReceiver
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.token.TokenRequestHandlerFactory
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClient] is responsible for handling the request for a session state from the Access Worldpay services.
 */
internal class AccessCheckoutClient(
    private val baseUrl: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener,
    private val lifecycleOwner: LifecycleOwner,
    private val tokenHandlerFactory: TokenRequestHandlerFactory
) : CheckoutClient {

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
     * @param tokenRequests the list of tokens that is being requested
     */
    override fun generateSession(cardDetails: CardDetails, tokenRequests: List<TokenRequest>) {
        val handlers = tokenHandlerFactory.getTokenHandlers()

        for (handler in handlers) {
            if (handler.canHandle(tokenRequests)) {
                handler.handle(cardDetails)
            }
        }
    }

}


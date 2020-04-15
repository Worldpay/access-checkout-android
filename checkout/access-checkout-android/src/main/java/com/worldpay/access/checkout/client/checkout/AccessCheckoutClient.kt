package com.worldpay.access.checkout.client.checkout

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.SessionReceiver
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClient] is responsible for handling the request for a session state from the Access Worldpay services.
 */
class AccessCheckoutClient(
    private val baseUrl: String,
    private val merchantId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener,
    private val lifecycleOwner: LifecycleOwner
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
     */
    override fun generateSessionState(cardDetails: CardDetails) {
        if (cardDetails.pan == null) {
            throw IllegalArgumentException("Expected pan but none provided")
        }

        if (cardDetails.expiryDate == null) {
            throw IllegalArgumentException("Expected expiry date but none provided")
        }

        if (cardDetails.cvv == null) {
            throw IllegalArgumentException("Expected cvv but none provided")
        }

        externalSessionResponseListener.onRequestStarted()
        val cardExpiryDate = CardSessionRequest.CardExpiryDate(cardDetails.expiryDate.month, cardDetails.expiryDate.year)
        val cardSessionRequest = CardSessionRequest(cardDetails.pan, cardExpiryDate, cardDetails.cvv, merchantId)
        val serviceIntent = Intent(context, SessionRequestService::class.java)

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cardSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, baseUrl)
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.verifiedTokens)
        context.startService(serviceIntent)
    }

}


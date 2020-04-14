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
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClient] is responsible for handling the request for a session state from the Access Worldpay services.
 */
class AccessCheckoutClient private constructor(
    private val baseUrl: String,
    private val merchantId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener,
    private val lifecycleOwner: LifecycleOwner
) {

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

    companion object {

        /**
         * Initialises the Access Checkout Android SDK
         *
         * @param baseUrl The URL of the root of Access Worldpay
         * @param merchantID The merchant identifier for using Access Worldpay services
         * @param sessionResponseListener The listener which will be notified when a session state is available
         * @param context The android [Context] object
         * @param lifecycleOwner The android [LifecycleOwner] object
         * @return an instance of AccessCheckoutClient
         */
        @JvmStatic
        fun init(
            baseUrl: String,
            merchantID: String,
            sessionResponseListener: SessionResponseListener,
            context: Context,
            lifecycleOwner: LifecycleOwner
        ): AccessCheckoutClient {
            return AccessCheckoutClient(
                baseUrl,
                merchantID,
                context,
                sessionResponseListener,
                lifecycleOwner
            )
        }
    }

    /**
     * Method which triggers a generate session state request to the Access Worldpay sessions API. The response will come back through the
     * [SessionResponseListener]
     *
     * @param pan the pan to submit
     * @param month the month to submit
     * @param year the year to submit
     * @param cvv the cvv to submit
     */

    fun generateSessionState(pan: String, month: Int, year: Int, cvv: String) {
        externalSessionResponseListener.onRequestStarted()
        val cardExpiryDate = CardSessionRequest.CardExpiryDate(month, year)
        val cardSessionRequest = CardSessionRequest(pan, cardExpiryDate, cvv, merchantId)
        val serviceIntent = Intent(context, SessionRequestService::class.java)

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cardSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, baseUrl)
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.verifiedTokens)
        context.startService(serviceIntent)
    }

}


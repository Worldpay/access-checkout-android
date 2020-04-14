package com.worldpay.access.checkout.client.checkout

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.api.discovery.AccessCheckoutDiscoveryClientFactory
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionReceiver
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutCVVClient] is responsible for handling the request for a session state from the Access Worldpay services.
 */
class AccessCheckoutCVVClient private constructor(
    private val baseUrl: String,
    private val merchantId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener,
    private val lifecycleOwner: LifecycleOwner
) {

    private val tag = "AccessCheckoutCVVClient"

    init {
        val checkoutSessionResponseListener =
            CheckoutSessionResponseListener(tag, externalSessionResponseListener)
        val sessionReceiver = SessionReceiver(checkoutSessionResponseListener)
        val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context)

        debugLog(tag, "Making request to discover endpoint")

        val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClientFactory.getClient()

        accessCheckoutDiscoveryClient.discover(
            baseUrl = baseUrl,
            discoverLinks = DiscoverLinks.sessions
        )

        ActivityLifecycleEventHandler(
            tag,
            sessionReceiver,
            lifecycleOwner,
            localBroadcastManagerFactory
        )
    }

    companion object {
        private const val TAG = "AccessCheckoutCVVClient"

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
        ): AccessCheckoutCVVClient {
            debugLog(TAG, "Making request to discover endpoint")
            val accessCheckoutDiscoveryClient = AccessCheckoutDiscoveryClientFactory.getClient()

            accessCheckoutDiscoveryClient.discover(
                baseUrl = baseUrl,
                discoverLinks = DiscoverLinks.sessions
            )

            return AccessCheckoutCVVClient(
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
     * @param cvv the cvv to submit
     */
    fun generateSessionState(cvv: String) {
        externalSessionResponseListener.onRequestStarted()
        val cvvSessionRequest = CVVSessionRequest(cvv, merchantId)
        val serviceIntent = Intent(context, SessionRequestService::class.java)

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cvvSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, baseUrl)
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.sessions)
        context.startService(serviceIntent)
    }

}


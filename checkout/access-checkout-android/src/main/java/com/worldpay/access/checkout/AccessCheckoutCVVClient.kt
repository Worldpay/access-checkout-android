package com.worldpay.access.checkout

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import com.worldpay.access.checkout.api.AccessCheckoutException
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
    private val baseURL: String,
    private val merchantID: String,
    private val context: Context,
    private val mListener: SessionResponseListener,
    private val lifecycleOwner: LifecycleOwner):
    LifecycleObserver, SessionResponseListener {

    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val localBroadcastManagerFactory = LocalBroadcastManagerFactory(context)
    private val sessionReceiver: SessionReceiver =
        SessionReceiver(this)
    init {
        onCreateHostRegistration()
    }

    companion object {
        private const val TAG = "AccessCheckoutClient"

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

            accessCheckoutDiscoveryClient.discover(baseUrl = baseUrl, discoverLinks = DiscoverLinks.verifiedTokens)
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
        mListener.onRequestStarted()
        val cvvSessionRequest = CVVSessionRequest(cvv, merchantID)
        val serviceIntent = Intent(context, SessionRequestService::class.java)

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cvvSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, baseURL)
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.sessions)
        context.startService(serviceIntent)
    }

    override fun onRequestStarted() {}

    override fun onRequestFinished(sessionState: String?, error: AccessCheckoutException?) {
        debugLog(TAG, "Received session reference")
        mListener.onRequestFinished(sessionState, error)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreateHostRegistration() {
        debugLog(TAG, "On Create")
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun startListener() {
        debugLog(TAG, "On Resume")
        localBroadcastManager = localBroadcastManagerFactory.createInstance()
        localBroadcastManager.registerReceiver(sessionReceiver, IntentFilter(SessionRequestService.ACTION_GET_SESSION))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun disconnectListener() {
        debugLog(TAG, "On Stop")
        localBroadcastManager.unregisterReceiver(sessionReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun removeObserver() {
        lifecycleOwner.lifecycle.removeObserver(this)
    }
}

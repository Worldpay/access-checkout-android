package com.worldpay.access.checkout.session

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.session.request.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionTypeBroadcastReceiver
import com.worldpay.access.checkout.session.request.broadcast.receivers.SessionTypeBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPES
import com.worldpay.access.checkout.session.request.handlers.SessionRequestHandlerFactory
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClientImpl] is responsible for handling the request for a session state from the Access Worldpay services.
 */
internal class AccessCheckoutClientImpl(
    private val sessionHandlerFactory: SessionRequestHandlerFactory,
    activityLifecycleObserverInitialiser: ActivityLifecycleObserverInitialiser,
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    private val context: Context
) : AccessCheckoutClient {

    init {
        activityLifecycleObserverInitialiser.initialise()
    }

    /**
     * Method which triggers a generate session state request to the Access Worldpay sessions API. The response will come back through the
     * [SessionResponseListener]
     *
     * @param cardDetails the cardDetails to submit - see [CardDetailsBuilder]
     * @param sessionTypes the list of tokens that is being requested
     */
    override fun generateSession(cardDetails: CardDetails, sessionTypes: List<SessionType>) {
        broadcastSessionTypeInfo(sessionTypes)

        val handlers = sessionHandlerFactory.getTokenHandlers()
        for (handler in handlers) {
            if (handler.canHandle(sessionTypes)) {
                handler.handle(cardDetails)
            }
        }
    }

    private fun broadcastSessionTypeInfo(sessionTypes: List<SessionType>) {
        val broadcastIntent = Intent(context, SessionTypeBroadcastReceiver::class.java)
        broadcastIntent.putExtra(NUMBER_OF_SESSION_TYPES, sessionTypes.size)
        broadcastIntent.action = SessionTypeBroadcastReceiver::class.java.name
        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }

}


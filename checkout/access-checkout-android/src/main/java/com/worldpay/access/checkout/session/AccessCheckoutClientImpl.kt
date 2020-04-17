package com.worldpay.access.checkout.session

import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.session.request.SessionRequestHandlerFactory
import com.worldpay.access.checkout.views.SessionResponseListener

/**
 * [AccessCheckoutClientImpl] is responsible for handling the request for a session state from the Access Worldpay services.
 */
internal class AccessCheckoutClientImpl(
    private val sessionHandlerFactory: SessionRequestHandlerFactory,
    activityLifecycleObserverInitialiser: ActivityLifecycleObserverInitialiser
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
        val handlers = sessionHandlerFactory.getTokenHandlers()

        for (handler in handlers) {
            if (handler.canHandle(sessionTypes)) {
                handler.handle(cardDetails)
            }
        }
    }

}


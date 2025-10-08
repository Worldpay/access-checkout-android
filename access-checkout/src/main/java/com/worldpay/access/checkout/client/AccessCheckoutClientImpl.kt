package com.worldpay.access.checkout.client

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.ValidationConfig
import com.worldpay.access.checkout.session.ActivityLifecycleObserver
import com.worldpay.access.checkout.session.ActivityLifecycleObserverInitialiser
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.NUM_OF_SESSION_TYPES_REQUESTED
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerFactory

internal class AccessCheckoutClientImpl(
    private val sessionHandlerFactory: SessionRequestHandlerFactory,
    activityLifecycleObserverInitialiser: ActivityLifecycleObserverInitialiser,
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    private val context: Context,
    private val checkoutId: String,
    private val baseUrl: String,
) : AccessCheckoutClient {

    private val activityLifecycleObserver: ActivityLifecycleObserver =
        activityLifecycleObserverInitialiser.initialise()

    override fun generateSessions(cardDetails: CardDetails, sessionTypes: List<SessionType>) {
        broadcastSessionTypeInfo(sessionTypes)

        val handlers = sessionHandlerFactory.getTokenHandlers()
        for (handler in handlers) {
            if (handler.canHandle(sessionTypes)) {
                handler.handle(cardDetails)
            }
        }
    }

    override fun initialiseValidation(validationConfiguration: ValidationConfig) {
        AccessCheckoutValidationInitialiser.initialise(checkoutId, baseUrl, validationConfiguration)
    }

    override fun dispose() {
        AccessCheckoutClientDisposer().dispose(this)
    }

    private fun broadcastSessionTypeInfo(sessionTypes: List<SessionType>) {
        val broadcastIntent = Intent(context, SessionBroadcastReceiver::class.java)
        broadcastIntent.putExtra(SessionBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPE_KEY, sessionTypes.size)
        broadcastIntent.action = NUM_OF_SESSION_TYPES_REQUESTED
        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }

    internal fun disposeInternal() {
        activityLifecycleObserver.onStop()
    }
}

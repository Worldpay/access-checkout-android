package com.worldpay.access.checkout.session.request.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.session.request.SessionRequestHandler
import com.worldpay.access.checkout.session.request.SessionRequestHandlerConfig
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

internal class PaymentsCvcSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
): SessionRequestHandler {

    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(PAYMENTS_CVC_SESSION)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.cvv, "cvv")

        cardDetails.cvv as String

        sessionRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        val cvvSessionRequest = CVVSessionRequest(cardDetails.cvv, sessionRequestHandlerConfig.getMerchantId())

        val serviceIntent = Intent(sessionRequestHandlerConfig.getContext(), SessionRequestService::class.java)
        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cvvSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, sessionRequestHandlerConfig.getBaseUrl())
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.sessions)

        sessionRequestHandlerConfig.getContext().startService(serviceIntent)
    }

}
package com.worldpay.access.checkout.session.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.session.api.request.CVVSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

/**
 * [PaymentsCvcSessionRequestHandler] is responsible for handling requests for a [PAYMENTS_CVC_SESSION]
 *
 * @property canHandle - returns true if list of [SessionType] contains a [PAYMENTS_CVC_SESSION]
 * @property handle - handles the request for a [PAYMENTS_CVC_SESSION]
 */
internal class PaymentsCvcSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
): SessionRequestHandler {

    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(PAYMENTS_CVC_SESSION)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.cvv, "cvv")

        cardDetails.cvv as String

        /**
         * Retrieves external [SessionResponseListener] and notifies that the request has started
         */
        sessionRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        val cvvSessionRequest =
            CVVSessionRequest(
                cardDetails.cvv,
                sessionRequestHandlerConfig.getMerchantId()
            )

        val serviceIntent = Intent(sessionRequestHandlerConfig.getContext(), SessionRequestService::class.java)

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl(sessionRequestHandlerConfig.getBaseUrl())
            .requestBody(cvvSessionRequest)
            .sessionType(PAYMENTS_CVC_SESSION)
            .discoverLinks(DiscoverLinks.sessions)
            .build()

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, sessionRequestInfo)

        sessionRequestHandlerConfig.getContext().startService(serviceIntent)
    }

}
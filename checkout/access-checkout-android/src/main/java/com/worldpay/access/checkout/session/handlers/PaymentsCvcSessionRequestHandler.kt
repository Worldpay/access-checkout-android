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

internal class PaymentsCvcSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
): SessionRequestHandler {
    /**
     * Accepts a list of [SessionType]s and returns True if the list contains a [PAYMENTS_CVC_SESSION]
     */
    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(PAYMENTS_CVC_SESSION)
    }

    /**
     * Accepts in a [CardDetails] object and validates that the mandatory fields for this [SessionType] are present
     *
     * Mandatory fields:
     * @param cardDetails.cvv
     */
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
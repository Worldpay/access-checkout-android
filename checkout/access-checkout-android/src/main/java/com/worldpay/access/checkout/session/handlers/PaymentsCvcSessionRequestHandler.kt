package com.worldpay.access.checkout.session.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

/**
 * [PaymentsCvcSessionRequestHandler] is responsible for handling requests for a [PAYMENTS_CVC_SESSION]
 *
 * @property sessionRequestHandlerConfig The [SessionRequestHandlerConfig] that should be used to retrieve request information
 */
internal class PaymentsCvcSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
): SessionRequestHandler {

    /**
     * Returns True if the list contains a [PAYMENTS_CVC_SESSION]
     *
     * @param sessionTypes A [List] of [SessionType] requested
     */
    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(PAYMENTS_CVC_SESSION)
    }

    /**
     * Validates that the mandatory fields for this [SessionType] are present
     *
     * @param cardDetails [CardDetails] object containing cvc
     */
    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.cvc, "cvc")

        cardDetails.cvc as String

        val cvcSessionRequest =
            CvcSessionRequest(
                cardDetails.cvc,
                sessionRequestHandlerConfig.getMerchantId()
            )

        val serviceIntent = Intent(sessionRequestHandlerConfig.getContext(), SessionRequestService::class.java)

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl(sessionRequestHandlerConfig.getBaseUrl())
            .requestBody(cvcSessionRequest)
            .sessionType(PAYMENTS_CVC_SESSION)
            .discoverLinks(DiscoverLinks.sessions)
            .build()

        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, sessionRequestInfo)

        sessionRequestHandlerConfig.getContext().startService(serviceIntent)
    }

}

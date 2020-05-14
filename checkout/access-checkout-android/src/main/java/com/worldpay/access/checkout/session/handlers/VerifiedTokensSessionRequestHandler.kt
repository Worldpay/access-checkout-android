package com.worldpay.access.checkout.session.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.ExpiryDate
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.PAYMENTS_CVC_SESSION
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

/**
 * [VerifiedTokensSessionRequestHandler] is responsible for handling requests for a [VERIFIED_TOKEN_SESSION]
 *
 * @property canHandle - returns true if list of [SessionType] contains a [VERIFIED_TOKEN_SESSION]
 * @property handle - handles the request for a [VERIFIED_TOKEN_SESSION]
 */
internal class VerifiedTokensSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
) : SessionRequestHandler {

    /**
     * Returns True if the list contains a [VERIFIED_TOKEN_SESSION]
     *
     * @param sessionTypes - a list of [SessionType] requested
     */
    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(VERIFIED_TOKEN_SESSION)
    }

    /**
     * Validates that the mandatory fields for this [SessionType] are present
     *
     * @param cardDetails  - [CardDetails]
     */
    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.pan, "pan")
        validateNotNull(cardDetails.expiryDate, "expiry date")
        validateNotNull(cardDetails.cvv, "cvv")

        sessionRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        val serviceIntent = Intent(sessionRequestHandlerConfig.getContext(), SessionRequestService::class.java)

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .baseUrl(sessionRequestHandlerConfig.getBaseUrl())
            .requestBody(createCardSessionRequest(cardDetails))
            .sessionType(VERIFIED_TOKEN_SESSION)
            .discoverLinks(DiscoverLinks.verifiedTokens)
            .build()

        serviceIntent.putExtra(REQUEST_KEY, sessionRequestInfo)

        sessionRequestHandlerConfig.getContext().startService(serviceIntent)
    }

    /**
     * Returns a [CardSessionRequest] object.
     *
     * @param cardDetails- [CardDetails] containing pan, expiryDate and cvv
     */
    private fun createCardSessionRequest(cardDetails: CardDetails): CardSessionRequest {
        cardDetails.pan as String
        cardDetails.expiryDate as ExpiryDate
        cardDetails.cvv as String

        val cardExpiryDate = CardExpiryDate(cardDetails.expiryDate.month, cardDetails.expiryDate.year)

        return CardSessionRequest(
            cardDetails.pan,
            cardExpiryDate,
            cardDetails.cvv,
            sessionRequestHandlerConfig.getMerchantId()
        )
    }

}
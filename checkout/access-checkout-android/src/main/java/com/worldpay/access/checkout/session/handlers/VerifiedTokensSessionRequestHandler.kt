package com.worldpay.access.checkout.session.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * [VerifiedTokensSessionRequestHandler] is responsible for handling requests for a [VERIFIED_TOKEN_SESSION]
 *
 * @property canHandle - returns true if list of [SessionType] contains a [VERIFIED_TOKEN_SESSION]
 * @property handle - handles the request for a [VERIFIED_TOKEN_SESSION]
 */
internal class VerifiedTokensSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
) : SessionRequestHandler {

    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(VERIFIED_TOKEN_SESSION)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.pan, "pan")
        validateNotNull(cardDetails.expiryDate, "expiry date")
        validateNotNull(cardDetails.cvc, "cvc")

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

    private fun createCardSessionRequest(cardDetails: CardDetails): CardSessionRequest {
        cardDetails.pan as String
        cardDetails.cvc as String

        val cardExpiryDate = CardExpiryDate(cardDetails.expiryDate!!.month, cardDetails.expiryDate.year)

        return CardSessionRequest(
            cardDetails.pan,
            cardExpiryDate,
            cardDetails.cvc,
            sessionRequestHandlerConfig.getMerchantId()
        )
    }

}

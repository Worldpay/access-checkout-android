package com.worldpay.access.checkout.session.request.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.api.session.SessionRequestInfo
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.ExpiryDate
import com.worldpay.access.checkout.client.SessionType
import com.worldpay.access.checkout.client.SessionType.VERIFIED_TOKEN_SESSION
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

internal class VerifiedTokensSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
) : SessionRequestHandler {

    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(VERIFIED_TOKEN_SESSION)
    }

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
package com.worldpay.access.checkout.token.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CVVSessionRequest
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.token.TokenRequest
import com.worldpay.access.checkout.client.token.TokenRequest.SESSION_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandler
import com.worldpay.access.checkout.token.TokenRequestHandlerConfig
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

internal class SessionTokenRequestRequestHandler(
    private val tokenRequestHandlerConfig: TokenRequestHandlerConfig
): TokenRequestHandler {

    override fun canHandle(tokenRequests: List<TokenRequest>): Boolean {
        return tokenRequests.contains(SESSION_TOKEN)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.cvv, "cvv")

        cardDetails.cvv as String

        tokenRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        val cvvSessionRequest = CVVSessionRequest(cardDetails.cvv, tokenRequestHandlerConfig.getMerchantId())

        val serviceIntent = Intent(tokenRequestHandlerConfig.getContext(), SessionRequestService::class.java)
        serviceIntent.putExtra(SessionRequestService.REQUEST_KEY, cvvSessionRequest)
        serviceIntent.putExtra(SessionRequestService.BASE_URL_KEY, tokenRequestHandlerConfig.getBaseUrl())
        serviceIntent.putExtra(SessionRequestService.DISCOVER_LINKS, DiscoverLinks.sessions)

        tokenRequestHandlerConfig.getContext().startService(serviceIntent)
    }

}
package com.worldpay.access.checkout.token.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.api.session.CardSessionRequest
import com.worldpay.access.checkout.api.session.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.api.session.SessionRequestService
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.BASE_URL_KEY
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.DISCOVER_LINKS
import com.worldpay.access.checkout.api.session.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.client.card.CardDetails
import com.worldpay.access.checkout.client.card.ExpiryDate
import com.worldpay.access.checkout.client.token.TokenRequest
import com.worldpay.access.checkout.client.token.TokenRequest.VERIFIED_TOKEN
import com.worldpay.access.checkout.token.TokenRequestHandler
import com.worldpay.access.checkout.token.TokenRequestHandlerConfig
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

internal class VerifiedTokenRequestRequestHandler(
    private val tokenRequestHandlerConfig: TokenRequestHandlerConfig
) : TokenRequestHandler {

    override fun canHandle(tokenRequests: List<TokenRequest>): Boolean {
        return tokenRequests.contains(VERIFIED_TOKEN)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.pan, "pan")
        validateNotNull(cardDetails.expiryDate, "expiry date")
        validateNotNull(cardDetails.cvv, "cvv")

        tokenRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        val cardSessionRequest = createCardSessionRequest(cardDetails)

        val serviceIntent = Intent(tokenRequestHandlerConfig.getContext(), SessionRequestService::class.java)
        serviceIntent.putExtra(REQUEST_KEY, cardSessionRequest)
        serviceIntent.putExtra(BASE_URL_KEY, tokenRequestHandlerConfig.getBaseUrl())
        serviceIntent.putExtra(DISCOVER_LINKS, DiscoverLinks.verifiedTokens)

        tokenRequestHandlerConfig.getContext().startService(serviceIntent)
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
            tokenRequestHandlerConfig.getMerchantId()
        )
    }

}
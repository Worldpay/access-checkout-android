package com.worldpay.access.checkout.session.handlers

import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.session.api.SessionRequestService
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CardSessionRequest
import com.worldpay.access.checkout.session.api.request.CardSessionRequest.CardExpiryDate
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * [CardSessionRequestHandler] is responsible for handling requests for a [CARD]
 *
 * @property canHandle - returns true if list of [SessionType] contains a [CARD]
 * @property handle - handles the request for a [CARD]
 */
internal class CardSessionRequestHandler(
    private val sessionRequestHandlerConfig: SessionRequestHandlerConfig
) : SessionRequestHandler {

    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(CARD)
    }

    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.pan, "pan")
        validateNotNull(cardDetails.expiryDate, "expiry date")
        validateNotNull(cardDetails.cvc, "cvc")

        val serviceIntent =
            Intent(sessionRequestHandlerConfig.getContext(), SessionRequestService::class.java)

        val sessionRequestInfo = SessionRequestInfo.Builder()
            .requestBody(createCardSessionRequest(cardDetails))
            .sessionType(CARD)
            .discoverLinks(DiscoverLinks.cardSessions)
            .build()

        serviceIntent.putExtra(REQUEST_KEY, sessionRequestInfo)

        sessionRequestHandlerConfig.getContext().startService(serviceIntent)
    }

    private fun createCardSessionRequest(cardDetails: CardDetails): CardSessionRequest {
        val cardExpiryDate =
            CardExpiryDate(cardDetails.expiryDate!!.month, cardDetails.expiryDate.year)

        return CardSessionRequest(
            cardDetails.pan!!,
            cardExpiryDate,
            cardDetails.cvc!!,
            sessionRequestHandlerConfig.getCheckoutId()
        )
    }
}

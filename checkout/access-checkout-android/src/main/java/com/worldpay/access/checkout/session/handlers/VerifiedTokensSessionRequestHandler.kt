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
     * Accepts a list of [SessionType]s and returns True if the list contains a [VERIFIED_TOKEN_SESSION]
     */
    override fun canHandle(sessionTypes: List<SessionType>): Boolean {
        return sessionTypes.contains(VERIFIED_TOKEN_SESSION)
    }

    /**
     * Accepts in a [CardDetails] object and validates that the mandatory fields for this [SessionType] are present
     *
     * Mandatory fields:
     * @param cardDetails.pan
     * @param cardDetails.expiryDate
     * @param cardDetails.cvv
     */
    override fun handle(cardDetails: CardDetails) {
        validateNotNull(cardDetails.pan, "pan")
        validateNotNull(cardDetails.expiryDate, "expiry date")
        validateNotNull(cardDetails.cvv, "cvv")

        /**
         * Retrieves external [SessionResponseListener] and notifies that the request has started
         */
        sessionRequestHandlerConfig.getExternalSessionResponseListener().onRequestStarted()

        /**
         * Sets the [Intent], builds a [SessionRequestInfo] object and adds that to the serviceIntent and starts the service
         */
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
     * Takes in [CardDetails] and returns a [CardSessionRequest] object.
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
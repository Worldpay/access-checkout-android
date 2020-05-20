package com.worldpay.access.checkout.session.handlers

import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType

internal interface SessionRequestHandler {

    fun canHandle(sessionTypes: List<SessionType>): Boolean

    fun handle(cardDetails: CardDetails)

}

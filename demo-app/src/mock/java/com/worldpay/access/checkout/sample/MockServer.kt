package com.worldpay.access.checkout.sample

import android.content.Context
import android.util.Log
import com.github.tomakehurst.wiremock.WireMockServer
import com.worldpay.access.checkout.sample.stub.BrandLogoMockStub.stubLogos
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.rootResourceMapping
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsCardRequest
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsPaymentCvcRequest
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsTokenRootRequest
import com.worldpay.access.checkout.test.mocks.AccessWPServiceWiremock

object MockServer {

    object Paths {
        const val SESSIONS_ROOT_PATH = "sessions"
        const val SESSIONS_PAYMENTS_CVC_PATH = "sessions/payments/cvc"
        const val SESSIONS_CARD_PATH = "sessions/card"

        const val CARD_LOGO_PATH = "access-checkout/assets"
        const val CARD_CONFIGURATION_PATH = "access-checkout/cardTypes.json"
    }

    fun defaultStubMappings(
        context: Context,
        server: WireMockServer
    ) {
        Log.d("MockServer", "Stubbing root endpoint with 200 response")
        try {
            server.stubFor(rootResourceMapping())
            // sessions service root endpoint
            stubSessionsTokenRootRequest()

            // card and cvc sessions endpoints
            stubSessionsCardRequest(context)
            stubSessionsPaymentCvcRequest(context)

            stubCardConfiguration(context)
            stubLogos(context)
        } catch (e: Exception) {
            Log.d("MockServer", "Failed to set up default stub mappings ${e.message}")
        }

    }
}

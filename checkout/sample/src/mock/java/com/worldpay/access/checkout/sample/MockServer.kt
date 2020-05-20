package com.worldpay.access.checkout.sample

import android.content.Context
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.worldpay.access.checkout.sample.stub.BrandLogoMockStub.stubLogos
import com.worldpay.access.checkout.sample.stub.CardConfigurationMockStub.stubCardConfiguration
import com.worldpay.access.checkout.sample.stub.RootResourseMockStub.rootResourceMapping
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsPaymentCvcRequest
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.stubSessionsTokenRootRequest
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.stubVerifiedTokenRootRequest
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.stubVerifiedTokenSessionRequest
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

object MockServer {

    private lateinit var context: Context
    private lateinit var wireMockServer: WireMockServer
    private lateinit var baseUrl: String

    private var hasStarted = false

    object Paths {
        const val SESSIONS_ROOT_PATH = "sessions"
        const val SESSIONS_PAYMENTS_CVC_PATH = "sessions/payments/cvc"

        const val VERIFIED_TOKENS_ROOT_PATH = "verifiedTokens"
        const val VERIFIED_TOKENS_SESSIONS_PATH = "verifiedTokens/sessions"

        const val CARD_LOGO_PATH = "access-checkout/assets"
        const val CARD_CONFIGURATION_PATH = "access-checkout/cardTypes.json"
    }

    fun startWiremock(context: Context, port: Int = 8080) {
        debugLog("MockServer", "Starting WireMock server!")

        MockServer.context = context

        wireMockServer = WireMockServer(WireMockConfiguration
            .options()
            .notifier(ConsoleNotifier(true))
            .port(port)
            .extensions(ResponseTemplateTransformer(false)))

        Thread(Runnable {
            wireMockServer.start()
            defaultStubMappings(
                context
            )
            hasStarted = true
        }).start()

        waitForWiremock()
    }

    fun stopWiremock() {
        wireMockServer.stop()
    }

    fun stubFor(mappingBuilder: MappingBuilder) {
        wireMockServer.stubFor(mappingBuilder)
    }

    fun getCurrentContext(): Context {
        return context
    }

    fun getBaseUrl(): String {
        return baseUrl
    }

    fun defaultStubMappings(context: Context) {
        debugLog("MockServer", "Stubbing root endpoint with 200 response")
        wireMockServer.stubFor(rootResourceMapping())

        // verified token
        stubVerifiedTokenRootRequest()
        stubVerifiedTokenSessionRequest(context)

        // sessions token
        stubSessionsTokenRootRequest()
        stubSessionsPaymentCvcRequest(context)

        stubCardConfiguration(context)
        stubLogos(context)
    }

    private fun waitForWiremock() {
        do {
            Thread.sleep(1000)
            debugLog("MockServer", "Waiting for wiremock to start!")
        } while (!hasStarted)
        debugLog("MockServer", "Started wiremock!!")
        baseUrl = wireMockServer.baseUrl()
    }

}
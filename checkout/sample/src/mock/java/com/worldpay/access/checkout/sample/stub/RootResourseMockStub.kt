package com.worldpay.access.checkout.sample.stub

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.util.logging.LoggingUtils

object RootResourseMockStub {

    private const val rootResourceResponse =
        """{
                    "_links": {
                        "payments:authorize": {
                            "href": "{{request.requestLine.baseUrl}}/payments/authorizations"
                        },
                        "service:payments": {
                            "href": "{{request.requestLine.baseUrl}}/payments"
                        },
                        "service:sessions": {
                          "href": "{{request.requestLine.baseUrl}}/sessions"
                        },
                        "service:tokens": {
                            "href": "{{request.requestLine.baseUrl}}/tokens"
                        },
                        "service:verifiedTokens": {
                            "href": "{{request.requestLine.baseUrl}}/verifiedTokens"
                        },
                        "curies": [
                            {
                                "href": "{{request.requestLine.baseUrl}}/rels/payments/{rel}",
                                "name": "payments",
                                "templated": true
                            }
                        ]
                    }
                }"""

    fun simulateRootResourceTemporaryServerError() {
        LoggingUtils.debugLog("MockServer", "Stubbing root endpoint with 500 error")
        val serviceAvailableState = "SERVICE_AVAILABLE"
        stubFor(
            WireMock.get("/")
                .inScenario("service re-discovery")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(serviceAvailableState)
                .willReturn(WireMock.aResponse().withStatus(500))
        )

        stubFor(
            rootResourceMapping()
                .inScenario("service re-discovery")
                .whenScenarioStateIs(serviceAvailableState)
        )
    }

    fun rootResourceMapping(): MappingBuilder {
        return WireMock.get("/")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(rootResourceResponse)
                    .withTransformers(ResponseTemplateTransformer.NAME)
            )
    }

}
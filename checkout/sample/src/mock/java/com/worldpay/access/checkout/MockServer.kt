package com.worldpay.access.checkout

import android.content.Context
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.worldpay.access.checkout.logging.LoggingUtils.Companion.debugLog

object MockServer {

    private lateinit var wireMockServer: WireMockServer

    private var hasStarted = false

    private const val verifiedTokensSessionResourcePath = "verifiedTokens/sessions"
    private const val cardConfigurationResourcePath = "access-checkout/cardConfiguration.json"

    private fun verifiedTokensResponse(context: Context) =
        """{
                  "_links": {
                    "verifiedTokens:session": {
                      "href": "${context.getString(R.string.session_reference)}"
                    },
                    "curies": [
                      {
                        "href": "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                        "name": "verifiedTokens",
                        "templated": true
                      }
                    ]
                  }
                }"""

    private const val serviceDiscoveryResponse =
        """{
                    "_links": {
                        "payments:authorize": {
                            "href": "{{request.requestLine.baseUrl}}/payments/authorizations"
                        },
                        "service:payments": {
                            "href": "{{request.requestLine.baseUrl}}/payments"
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

    private const val topLevelServiceResourceResponse = """{
                "_links": {
                    "verifiedTokens:recurring": {
                        "href": "{{request.requestLine.baseUrl}}/verifiedTokens/recurring"
                    },
                    "verifiedTokens:cardOnFile": {
                        "href": "{{request.requestLine.baseUrl}}/verifiedTokens/cardOnFile"
                    },
                    "verifiedTokens:sessions": {
                        "href": "{{request.requestLine.baseUrl}}/$verifiedTokensSessionResourcePath"
                    },
                "resourceTree": {
                    "href": "{{request.requestLine.baseUrl}}/rels/verifiedTokens/resourceTree.json"
                },
                "curies": [{
                    "href": "{{request.requestLine.baseUrl}}/rels/verifiedTokens/{rel}.json",
                    "name": "verifiedTokens",
                    "templated": true
                }]
            }
        }"""

    fun startWiremock(context: Context, port: Int = 8080) {
        debugLog("MockServer", "Starting WireMock server!")
        wireMockServer = WireMockServer(WireMockConfiguration
            .options()
            .notifier(ConsoleNotifier(true))
            .port(port)
            .extensions(ResponseTemplateTransformer(false)))

        Thread(Runnable {
            wireMockServer.start()
            defaultStubMappings(context)
            hasStarted = true
        }).start()

        waitForWiremock()
    }

    fun stopWiremock() {
        wireMockServer.stop()
    }

    fun defaultStubMappings(context: Context) {
        stubRootResource()
        stubServiceRootResource()
        wireMockServer.stubFor(
            post(urlEqualTo("/$verifiedTokensSessionResourcePath"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(AnythingPattern())
                .willReturn(validSessionResponseWithDelay(context, 2000))
        )
        stubCardConfiguration(context)
    }

    fun simulateDelayedResponse(context: Context) {
        wireMockServer.stubFor(
            post(urlEqualTo("/$verifiedTokensSessionResourcePath"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${context.getString(R.string.long_delay_card_number)}')]"))
                .willReturn(validSessionResponseWithDelay(context, 7000))
        )
    }

    fun simulateErrorResponse(context: Context) {
        wireMockServer.stubFor(
            post(urlEqualTo("/$verifiedTokensSessionResourcePath"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withRequestBody(MatchesJsonPathPattern("$[?(@.cardNumber=='${context.getString(R.string.error_response_card_number)}')]"))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(400)
                        .withBody(
                            """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "$.cardNumber"
                                    }
                                ]
                            }""".trimIndent()
                        )
                )
        )
    }

    fun simulateRootResourceTemporaryServerError() {
        debugLog("MockServer", "Stubbing root endpoint with 500 error")
        val serviceAvailableState = "SERVICE_AVAILABLE"
        wireMockServer.stubFor(
            WireMock.get("/")
                .inScenario("service re-discovery")
                .whenScenarioStateIs(Scenario.STARTED)
                .willSetStateTo(serviceAvailableState)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        wireMockServer.stubFor(
            rootResourceMapping()
                .inScenario("service re-discovery")
                .whenScenarioStateIs(serviceAvailableState)
        )
    }

    fun simulateHttpRedirect(context: Context) {
        val newLocation = "newVerifiedTokensLocation/sessions"
        wireMockServer.stubFor(
            post(urlEqualTo("/$verifiedTokensSessionResourcePath"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(308)
                        .withHeader("Location", "${wireMockServer.baseUrl()}/$newLocation")))

        wireMockServer.stubFor(
            post(urlEqualTo("/$newLocation"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(AnythingPattern())
                .willReturn(validSessionResponseWithDelay(context, 2000))
        )
    }

    fun stubCardConfiguration(context: Context) {
        wireMockServer.stubFor(get("/$cardConfigurationResourcePath")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(context.resources.openRawResource(R.raw.card_configuration_file).reader(Charsets.UTF_8).readText())
            ))
    }

    fun simulateCardConfigurationServerError() {
        wireMockServer.stubFor(get("/$cardConfigurationResourcePath")
            .willReturn(
                aResponse()
                    .withStatus(500)
            ))
    }

    private fun stubRootResource() {
        debugLog("MockServer", "Stubbing root endpoint with 200 response")
        wireMockServer.stubFor(rootResourceMapping())
    }

    private fun rootResourceMapping(): MappingBuilder {
        return get("/")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(serviceDiscoveryResponse)
                    .withTransformers("response-template")
            )
    }

    private fun validSessionResponseWithDelay(context: Context, delay: Int): ResponseDefinitionBuilder? {
        return aResponse()
            .withFixedDelay(delay)
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withHeader(
                "Location",
                context.getString(R.string.session_reference)
            )
            .withBody(verifiedTokensResponse(context))
    }

    private fun stubServiceRootResource() {
        wireMockServer.stubFor(
            get("/verifiedTokens")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(topLevelServiceResourceResponse)
                        .withTransformers("response-template")
                )
        )
    }

    private fun waitForWiremock() {
        do {
            Thread.sleep(1000)
            debugLog("MockServer", "Waiting for wiremock to start!")
        } while (!hasStarted)
        debugLog("MockServer", "Started wiremock!!")

    }

}
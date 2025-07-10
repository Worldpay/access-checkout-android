package com.worldpay.access.checkout.sample.stub

import android.content.Context
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.MatchesJsonPathPattern
import com.worldpay.access.checkout.sample.MockServer
import com.worldpay.access.checkout.sample.MockServer.Paths.SESSIONS_PAYMENTS_CVC_PATH
import com.worldpay.access.checkout.sample.MockServer.Paths.SESSIONS_ROOT_PATH
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.SessionsResponses.defaultResponse
import com.worldpay.access.checkout.sample.stub.SessionsMockStub.SessionsResponses.validResponseWithDelay

object SessionsMockStub {

    const val SESSIONS_MEDIA_TYPE = "application/vnd.worldpay.sessions-v1.hal+json"

    fun stubSessionsTokenRootRequest() {
        stubFor(
            get("/$SESSIONS_ROOT_PATH")
                .willReturn(defaultResponse())
        )
    }

    fun stubSessionsCardRequest(context: Context) {
        stubFor(
            post(urlEqualTo("/${MockServer.Paths.SESSIONS_CARD_PATH}"))
                .withHeader("Accept", equalTo(SESSIONS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(SESSIONS_MEDIA_TYPE))
                .withHeader(
                    "X-WP-SDK",
                    matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$")
                )
                .withRequestBody(MatchesJsonPathPattern("cardNumber", matching("^[\\d]+$")))
                .willReturn(
                    validResponseWithDelay(context)
                )
        )
    }

    fun stubSessionsPaymentCvcRequest(context: Context) {
        stubFor(
            post(urlEqualTo("/$SESSIONS_PAYMENTS_CVC_PATH"))
                .withHeader("Accept", equalTo(SESSIONS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(SESSIONS_MEDIA_TYPE))
                .withHeader(
                    "X-WP-SDK",
                    matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$")
                )
                .withRequestBody(AnythingPattern())
                .willReturn(validResponseWithDelay(context))
        )
    }

    fun simulateHttpRedirect(context: Context) {
        val newLocation = "newSessionsLocation/sessions"
        stubFor(
            post(urlEqualTo("/${MockServer.Paths.SESSIONS_CARD_PATH}"))
                .withHeader("Accept", equalTo(SESSIONS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(SESSIONS_MEDIA_TYPE))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(308)
                        .withHeader("Location", "${MockServer.getBaseUrl()}/$newLocation")
                )
        )

        stubFor(
            post(urlEqualTo("/$newLocation"))
                .withHeader("Accept", equalTo(SESSIONS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(SESSIONS_MEDIA_TYPE))
                .withHeader(
                    "X-WP-SDK",
                    matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$")
                )
                .withRequestBody(AnythingPattern())
                .willReturn(
                    validResponseWithDelay(context)
                )
        )
    }

    object SessionsResponses {

        private fun sessionsResourceResponse(context: Context) =
            """{ 
                  "_links": { 
                    "sessions:session": { 
                        "href": "${context.getString(R.string.cvc_session_reference)}"                        }, 
                    "curies": [ 
                      { 
                        "href": "{{request.requestLine.baseUrl}}/rels/sessions/{rel}.json", 
                        "name": "sessions",
                        "templated": true
                      } 
                    ] 
                  }
                }""".trimMargin()

        private const val sessionsResourceResponse = """
                {
                  "_links": {
                    "sessions:card": {
                      "href": "{{request.requestLine.baseUrl}}/sessions/card"
                    },
                    "sessions:paymentsCvc": {
                      "href": "{{request.requestLine.baseUrl}}/sessions/payments/cvc"
                    },
                    "resourceTree": {
                      "href": "{{request.requestLine.baseUrl}}/rels/sessions/resourceTree.json"
                    },
                    "curies": [
                      {
                        "href": "{{request.requestLine.baseUrl}}/rels/sessions/{rel}.json",
                        "name": "sessions",
                        "templated": true
                      }
                    ]
                  }
                }"""

        fun defaultResponse(): ResponseDefinitionBuilder? {
            return aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(sessionsResourceResponse)
                .withTransformers(ResponseTemplateTransformer.NAME)
        }

        fun validResponseWithDelay(context: Context, delay: Int = 0): ResponseDefinitionBuilder? {
            return aResponse()
                .withFixedDelay(delay)
                .withStatus(201)
                .withHeader("Content-Type", "application/vnd.worldpay.sessions-v1.hal+json")
                .withHeader("Location", context.getString(R.string.cvc_session_reference))
                .withBody(
                    sessionsResourceResponse(
                        context
                    )
                )
        }
    }
}

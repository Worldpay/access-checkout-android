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
import com.worldpay.access.checkout.sample.MockServer.Paths.VERIFIED_TOKENS_ROOT_PATH
import com.worldpay.access.checkout.sample.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.sample.MockServer.getBaseUrl
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VerifiedTokenResponses.defaultResponse
import com.worldpay.access.checkout.sample.stub.VerifiedTokenMockStub.VerifiedTokenResponses.validResponseWithDelay

object VerifiedTokenMockStub {

    const val VERIFIED_TOKENS_MEDIA_TYPE = "application/vnd.worldpay.verified-tokens-v1.hal+json"

    fun stubVerifiedTokenSessionRequest(context: Context) {
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(MatchesJsonPathPattern("cardNumber", matching("^[\\d]+$")))
                .willReturn(validResponseWithDelay(context, 2000))
        )
    }

    fun stubVerifiedTokenRootRequest() {
        stubFor(
            get("/$VERIFIED_TOKENS_ROOT_PATH")
                .willReturn(defaultResponse())
        )
    }

    fun simulateHttpRedirect(context: Context) {
        val newLocation = "newVerifiedTokensLocation/sessions"
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(VERIFIED_TOKENS_MEDIA_TYPE))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(308)
                        .withHeader("Location", "${getBaseUrl()}/$newLocation")
                )
        )

        stubFor(
            post(urlEqualTo("/$newLocation"))
                .withHeader("Accept", equalTo(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("Content-Type", containing(VERIFIED_TOKENS_MEDIA_TYPE))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(AnythingPattern())
                .willReturn(validResponseWithDelay(context, 2000))
        )
    }

    object VerifiedTokenResponses {

        private fun verifiedTokensResponse(context: Context) =
            """{
                  "_links": {
                    "verifiedTokens:session": {
                      "href": "${context.getString(R.string.verified_token_session_reference)}"
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

        private const val verifiedTokenResourceResponse = """{
                "_links": {
                    "verifiedTokens:cardOnFile": {
                        "href": "{{request.requestLine.baseUrl}}/verifiedTokens/cardOnFile"
                    },
                    "verifiedTokens:sessions": {
                        "href": "{{request.requestLine.baseUrl}}/$VERIFIED_TOKENS_SESSIONS_PATH"
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

        fun defaultResponse(): ResponseDefinitionBuilder? {
            return aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(verifiedTokenResourceResponse)
                .withTransformers(ResponseTemplateTransformer.NAME)
        }

        fun validResponseWithDelay(context: Context, delay: Int): ResponseDefinitionBuilder? {
            return aResponse()
                .withFixedDelay(delay)
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withHeader("Location", context.getString(R.string.verified_token_session_reference))
                .withBody(
                    verifiedTokensResponse(
                        context
                    )
                )
        }
    }
}

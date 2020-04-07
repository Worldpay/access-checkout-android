package com.worldpay.access.checkout

import android.content.Context
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.worldpay.access.checkout.MockServer.Paths.VERIFIED_TOKENS_SESSIONS_PATH
import com.worldpay.access.checkout.MockServer.getBaseUrl
import com.worldpay.access.checkout.MockServer.stubFor
import com.worldpay.access.checkout.VerifiedTokenMockStub.VerifiedTokenResponses.defaultResponse
import com.worldpay.access.checkout.VerifiedTokenMockStub.VerifiedTokenResponses.validResponseWithDelay

object VerifiedTokenMockStub {

    private const val verifiedTokensSessionResourcePath = "verifiedTokens/sessions"

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

    private const val verifiedTokenResourceResponse = """{
                "_links": {
                    "verifiedTokens:cardOnFile": {
                        "href": "{{request.requestLine.baseUrl}}/verifiedTokens/cardOnFile"
                    },
                    "verifiedTokens:sessions": {
                        "href": "{{request.requestLine.baseUrl}}/${verifiedTokensSessionResourcePath}"
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

    fun stubVerifiedToken(wireMockServer: WireMockServer, context: Context) {
        wireMockServer.stubFor(
            post(urlEqualTo("/$verifiedTokensSessionResourcePath"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(AnythingPattern())
                .willReturn(validResponseWithDelay(context, 2000))
        )
    }

    fun stubVerifiedTokenResourceRequest(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            get("/verifiedTokens")
                .willReturn(defaultResponse())
        )
    }

    fun simulateHttpRedirect(context: Context) {
        val newLocation = "newVerifiedTokensLocation/sessions"
        stubFor(
            post(urlEqualTo("/$VERIFIED_TOKENS_SESSIONS_PATH"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .willReturn(
                    aResponse()
                        .withFixedDelay(2000)
                        .withStatus(308)
                        .withHeader("Location", "${getBaseUrl()}/$newLocation")))

        stubFor(
            post(urlEqualTo("/$newLocation"))
                .withHeader("Accept", equalTo("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("Content-Type", containing("application/vnd.worldpay.verified-tokens-v1.hal+json"))
                .withHeader("X-WP-SDK", matching("^access-checkout-android/[\\d]+.[\\d]+.[\\d]+(-SNAPSHOT)?\$"))
                .withRequestBody(AnythingPattern())
                .willReturn(validResponseWithDelay(context, 2000))
        )
    }

    object VerifiedTokenResponses {

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
                .withHeader("Location", context.getString(R.string.session_reference))
                .withBody(verifiedTokensResponse(context))
        }

    }

}
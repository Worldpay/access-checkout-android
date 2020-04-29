package com.worldpay.access.checkout.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer

object ApiDiscoveryStubs {

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
                        "href": "{{request.requestLine.baseUrl}}/verifiedTokens/sessions"
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

    fun stubServiceDiscoveryResponses() {
        WireMock.stubFor(
            rootResponseMapping()
        )

        WireMock.stubFor(
            verifiedTokensMapping()
        )
    }

    fun rootResponseMapping(): MappingBuilder {
        return WireMock.get("/")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(serviceDiscoveryResponse)
                    .withTransformers(ResponseTemplateTransformer.NAME)
            )
    }

    fun verifiedTokensMapping(): MappingBuilder {
        return WireMock.get("/verifiedTokens")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(topLevelServiceResourceResponse)
                    .withTransformers(ResponseTemplateTransformer.NAME)
            )
    }
}
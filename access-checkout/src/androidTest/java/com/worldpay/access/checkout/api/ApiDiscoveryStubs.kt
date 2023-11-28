package com.worldpay.access.checkout.api

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
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
                        "service:sessions": {
                            "href": "{{request.requestLine.baseUrl}}/sessions"
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
                    "sessions:card": {
                        "href": "{{request.requestLine.baseUrl}}/sessions/card"
                    },
                    "sessions:paymentsCvc": {
                        "href": "{{request.requestLine.baseUrl}}/sessions/payments/cvc"
                    },
                    "resourceTree": {
                        "href": "{{request.requestLine.baseUrl}}/rels/sessions/resourceTree.json"
                    },
                    "curies": [{
                        "href": "{{request.requestLine.baseUrl}}/rels/sessions/{rel}.json",
                        "name": "sessions",
                        "templated": true
                    }]
                }
            }"""

    fun stubServiceDiscoveryResponses() {
        stubFor(rootResponseMapping())
        stubFor(cardSessionsMapping())
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

    fun cardSessionsMapping(): MappingBuilder {
        return WireMock.get("/sessions")
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(topLevelServiceResourceResponse)
                    .withTransformers(ResponseTemplateTransformer.NAME)
            )
    }
}

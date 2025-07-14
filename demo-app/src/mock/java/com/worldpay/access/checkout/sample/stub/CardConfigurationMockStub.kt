package com.worldpay.access.checkout.sample.stub

import android.content.Context
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.worldpay.access.checkout.sample.MockServer
import com.worldpay.access.checkout.sample.MockServer.Paths.CARD_CONFIGURATION_PATH
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.test.mocks.AccessWPServiceWiremock

object CardConfigurationMockStub {

    fun stubCardConfiguration(context: Context) {
        AccessWPServiceWiremock.server!!.stubFor(
            get("/$CARD_CONFIGURATION_PATH")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            context.resources.openRawResource(R.raw.card_types)
                                .reader(Charsets.UTF_8).readText()
                        )
                        .withTransformers(ResponseTemplateTransformer.NAME)
                )
        )
    }

    fun stubCardConfigurationWithDelay(delay: Int = 0) {
        val json = """
                   [
                      {
                        "name": "mastercard",
                        "pattern": "^(5[1-5]|2[2-7])\\d*${'$'}",
                        "panLengths": [
                          16
                        ],
                        "cvvLength": 3,
                        "images": [
                          {
                            "type": "image/svg+xml",
                            "url": "${AccessWPServiceWiremock.server!!.url("/")}access-checkout/assets/mastercard.svg"
                          }
                        ]
                      }
                ]
        """.trimIndent()

        AccessWPServiceWiremock.server!!.stubFor(
            get("/$CARD_CONFIGURATION_PATH")
                .willReturn(
                    aResponse()
                        .withFixedDelay(delay)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)
                )
        )
    }

    fun simulateCardConfigurationServerError() {
        AccessWPServiceWiremock.server!!.stubFor(
            get("/$CARD_CONFIGURATION_PATH")
                .willReturn(
                    aResponse()
                        .withStatus(500)
                )
        )
    }
}

package com.worldpay.access.checkout.sample.stub

import android.content.Context
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.google.gson.GsonBuilder
import com.worldpay.access.checkout.model.CardConfiguration
import com.worldpay.access.checkout.sample.MockServer.Paths.CARD_CONFIGURATION_PATH
import com.worldpay.access.checkout.sample.MockServer.stubFor
import com.worldpay.access.checkout.sample.R

object CardConfigurationMockStub {

    fun stubCardConfiguration(context: Context) {
        stubFor(get("/$CARD_CONFIGURATION_PATH")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(context.resources.openRawResource(R.raw.card_types).reader(Charsets.UTF_8).readText())
                    .withTransformers(ResponseTemplateTransformer.NAME)
            ))
    }

    fun stubCardConfigurationWithDelay(cardConfiguration: CardConfiguration, delay: Int = 0) {
        val json = """
              {
               "brands": 
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
                            "url": "${MockServer.getBaseUrl()}"/access-checkout/assets/mastercard.svg"
                          }
                        ]
                      }
                ]
              }
            """.trimIndent()

        stubFor(get("/$CARD_CONFIGURATION_PATH")
            .willReturn(
                aResponse()
                    .withFixedDelay(delay)
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(json)
            ))
    }

    fun simulateCardConfigurationServerError() {
        stubFor(get("/$CARD_CONFIGURATION_PATH")
            .willReturn(
                aResponse()
                    .withStatus(500)
            ))
    }


}
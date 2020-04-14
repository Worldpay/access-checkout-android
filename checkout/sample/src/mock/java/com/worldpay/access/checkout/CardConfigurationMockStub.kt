package com.worldpay.access.checkout

import android.content.Context
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.google.gson.GsonBuilder
import com.worldpay.access.checkout.MockServer.Paths.CARD_CONFIGURATION_PATH
import com.worldpay.access.checkout.MockServer.stubFor
import com.worldpay.access.checkout.model.CardConfiguration

object CardConfigurationMockStub {

    fun stubCardConfiguration(context: Context) {
        stubFor(get("/$CARD_CONFIGURATION_PATH")
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(context.resources.openRawResource(R.raw.card_configuration_file).reader(Charsets.UTF_8).readText())
                    .withTransformers(ResponseTemplateTransformer.NAME)
            ))
    }

    fun stubCardConfigurationWithDelay(cardConfiguration: CardConfiguration, delay: Int = 0) {
        val json = GsonBuilder().create().toJson(cardConfiguration)
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
package com.worldpay.access.checkout.api

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.worldpay.access.checkout.api.configuration.CardConfigurationAsyncTask
import com.worldpay.access.checkout.model.CardConfiguration
import org.awaitility.Awaitility.await
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CardConfigurationIntegrationTest {

    private val cardConfigurationEndpoint = "/access-checkout/cardConfiguration.json"

    private val cardConfigJson = """
        {
            "defaults": {
                "pan": {
                    "matcher": "^\\d{0,19}${'$'}",
                    "minLength": 13,
                    "maxLength": 19
                },
                "cvv": {
                    "matcher": "^\\d{0,4}${'$'}",
                    "minLength": 3,
                    "maxLength": 4
                },
                "month": {
                    "matcher": "^0[1-9]{0,1}${'$'}|^1[0-2]{0,1}${'$'}",
                    "minLength": 2,
                    "maxLength": 2
                },
                "year": {
                    "matcher": "^\\d{0,2}${'$'}",
                    "minLength": 2,
                    "maxLength": 2
                }
            },
            "brands": [
                {
                    "name": "test",
                    "image": "test_logo",
                    "cvv": {
                        "matcher": "^\\d{0,3}${'$'}",
                        "validLength": 3
                    },
                    "pans": [
                        {
                            "matcher": "^4\\d{0,15}",
                            "validLength": 16,
                            "subRules": [
                                {
                                    "matcher": "^(413600|444509|444550|450603|450617|450628|450636|450640|450662|463100|476142|476143|492901|492920|492923|492928|492937|492939|492960)\\d{0,7}",
                                    "validLength": 13
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    """.trimIndent()

    @get:Rule
    var wireMockRule = WireMockRule(
        WireMockConfiguration
            .options()
            .port(8090)
            .extensions(ResponseTemplateTransformer(false))
            .notifier(ConsoleNotifier(true))
    )

    @Test
    fun givenCardConfigurationAvailable_ThenCardConfigurationAsyncTaskCanFetchCardConfiguration() {
        stubFor(get(cardConfigurationEndpoint)
            .willReturn(
                WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(cardConfigJson)
            ))

        var cardConfiguration: CardConfiguration? = null

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                cardConfiguration = response
            }
        }

        val asyncTask = CardConfigurationAsyncTask(callback)

        asyncTask.execute(wireMockRule.baseUrl())

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("CardConfigurationIntegrationTest", "Got card configuration: $cardConfiguration")
            cardConfiguration != null
        }
    }

    @Test
    fun givenAnErrorFetchingCardConfiguration_ThenExceptionIsPassedBackToCallback() {
        stubFor(
            WireMock.get(cardConfigurationEndpoint)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        var exception: Exception? = null

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                exception = error
            }
        }

        val asyncTask = CardConfigurationAsyncTask(callback)

        asyncTask.execute(wireMockRule.baseUrl())

        await().atMost(5, TimeUnit.SECONDS).until {
            Log.d("CardConfigurationIntegrationTest", "Error received: $exception")
            exception is AccessCheckoutException.AccessCheckoutConfigurationException &&
                    exception?.message == "There was an error when trying to fetch the card configuration"
        }
    }
}
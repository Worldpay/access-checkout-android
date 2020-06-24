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
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationAsyncTask
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import org.awaitility.Awaitility.await
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class CardConfigurationIntegrationTest {

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = """
                   [
                    {
                        "name": "visa",
                        "pattern": "/^4\\d*$/",
                        "panLengths": [
                          16,
                          18,
                          19
                        ],
                        "cvvLength": 3,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/visa.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/visa.svg"
                          }
                        ]
                    },
                      {
                        "name": "mastercard",
                        "pattern": "^(5[1-5]|2[2-7])\\d*${'$'}",
                        "panLengths": [
                          16
                        ],
                        "cvvLength": 3,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/mastercard.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/mastercard.svg"
                          }
                        ]
                      },
                      {
                        "name": "amex",
                        "pattern": "^3[47]\\d*${'$'}",
                        "panLengths": [
                          15
                        ],
                        "cvvLength": 4,
                        "images": [
                          {
                            "type": "image/png",
                            "url": "<BASE_URL>/amex.png"
                          },
                          {
                            "type": "image/svg+xml",
                            "url": "<BASE_URL>/amex.svg"
                          }
                        ]
                      }
                ]
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
            get(cardConfigurationEndpoint)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        var assertionDone = false
        val expectedException = AccessCheckoutException("Error message was: Server Error")

        val callback = object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                assertEquals(expectedException, error)
                assertNull(response)
                assertionDone = true
            }
        }

        val asyncTask = CardConfigurationAsyncTask(callback)

        asyncTask.execute(wireMockRule.baseUrl())

        await().atMost(5, TimeUnit.SECONDS).until { assertionDone }
    }
}
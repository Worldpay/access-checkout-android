package com.worldpay.access.checkout.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.worldpay.access.checkout.api.MockServer.getBaseUrl
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CardConfigurationIntegrationTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val applicationContext = InstrumentationRegistry.getInstrumentation().context.applicationContext

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

    @Before
    fun setUp() {
        MockServer.startWiremock(applicationContext, 8443)
    }

    @After
    fun tearDown() {
        MockServer.stopWiremock()
    }

    @Test
    fun shouldBeAbleToRetrieveCardConfiguration() = runAsBlockingTest {
        stubFor(
            get(cardConfigurationEndpoint)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(cardConfigJson)
                )
        )

        val cardConfigurationClient = CardConfigurationClient(getBaseUrl())
        val cardConfig = cardConfigurationClient.getCardConfiguration()

        assertNotNull(cardConfig)
    }

    @Test
    fun shouldThrowExceptionWhenFailingToGetCardConfiguration() = runAsBlockingTest {
        stubFor(
            get(cardConfigurationEndpoint)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(500)
                )
        )

        try {
            val cardConfigurationClient = CardConfigurationClient(getBaseUrl())
            cardConfigurationClient.getCardConfiguration()
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals("Error message was: Server Error", ace.message)
        } catch (ex: Exception) {
            fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
        }
    }
}

package com.worldpay.access.checkout.api

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.worldpay.access.checkout.R
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
                    .withBody(getInstrumentation().context.resources.openRawResource(R.raw.card_configuration).reader(Charsets.UTF_8).readText())
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
            cardConfiguration != null && !cardConfiguration!!.isEmpty()
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
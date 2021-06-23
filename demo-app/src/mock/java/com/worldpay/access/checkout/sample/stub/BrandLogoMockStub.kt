package com.worldpay.access.checkout.sample.stub

import android.content.Context
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.worldpay.access.checkout.sample.MockServer.Paths.CARD_LOGO_PATH
import java.io.IOException

object BrandLogoMockStub {

    fun stubLogos(context: Context) {
        val images = listOf("visa.svg", "mastercard.svg", "amex.svg", "maestro.svg", "diners.svg", "discover.svg", "jcb.svg")
        images.forEach {
            stubFor(
                WireMock
                    .get("/$CARD_LOGO_PATH/$it")
                    .willReturn(
                        WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "image/svg+xml")
                            .withHeader("Cache-Control", "max-age=300")
                            .withBody(
                                getAsset(
                                    context,
                                    it
                                )
                            )
                    )
            )
        }
    }

    private fun getAsset(context: Context, assetPath: String): String {
        try {
            val inputStream = context.assets.open(assetPath)
            return inputStream.reader().readText()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}

package com.worldpay.access.checkout.client.testutil

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.spy
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.robolectric.shadows.ShadowInstrumentation
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

open class AbstractValidationIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    protected lateinit var pan: EditText
    protected lateinit var cvc: EditText
    protected lateinit var expiryDate: EditText

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private val server = MockWebServer()

    protected lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()

    @Before
    fun setup() {
        pan = EditText(context)
        pan.id = 1
        expiryDate = EditText(context)
        expiryDate.id = 2
        cvc = EditText(context)
        cvc.id = 3

        reset(lifecycle, lifecycleOwner)

        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())

        server.enqueue(MockResponse().setBody(cardConfigJson))
        server.useHttps(getSslContext().socketFactory, false)
        server.start()

        cardValidationListener = spy(CardValidationListener())

        reset(cardValidationListener)
    }

    @After
    fun tearDown() {
        server.shutdown()
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
    }

    protected fun initialiseWithoutAcceptedCardBrands(enablePanFormatting: Boolean = false) {
        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .baseUrl(baseUrl)
            .lifecycleOwner(lifecycleOwner)

        if (enablePanFormatting) {
            cardValidationConfig.enablePanFormatting()
        }

        AccessCheckoutValidationInitialiser.initialise(
            cardValidationConfig.build()
        )
    }

    protected fun initialiseWithAcceptedCardBrands(cardBrands: Array<String>) {
        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .acceptedCardBrands(cardBrands)
            .validationListener(cardValidationListener)
            .baseUrl(baseUrl)
            .lifecycleOwner(lifecycleOwner)
            .build()

        AccessCheckoutValidationInitialiser.initialise(
            cardValidationConfig
        )
    }

    private fun getSslContext(): SSLContext {
        val stream = TrustAllSSLSocketFactory::class.java.getResource("wiremock.bks")?.openStream()
        val serverKeyStore = KeyStore.getInstance("BKS")
        serverKeyStore.load(stream, "".toCharArray())

        val kmfAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
        val kmf = KeyManagerFactory.getInstance(kmfAlgorithm)
        kmf.init(serverKeyStore, "password".toCharArray())

        val trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgorithm)
        trustManagerFactory.init(serverKeyStore)

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(kmf.keyManagers, trustManagerFactory.trustManagers, null)
        return sslContext
    }

}

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
import com.worldpay.access.checkout.client.validation.model.CardBrands
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@RunWith(RobolectricTestRunner::class)
open class AbstractValidationIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    protected val pan = EditText(context)
    protected val cvc = EditText(context)
    protected val expiryDate = EditText(context)

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private val server = MockWebServer()

    protected lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()

    @Before
    fun setup() {
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
        val stateManager = CardValidationStateManager
        stateManager.panValidationState.notificationSent = false
        stateManager.panValidationState.validationState = false
        stateManager.expiryDateValidationState.notificationSent = false
        stateManager.expiryDateValidationState.validationState = false
        stateManager.cvcValidationState.notificationSent = false
        stateManager.cvcValidationState.validationState = false
        server.shutdown()
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
    }

    protected fun initialiseWithoutAcceptedCardBrands() {
        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .baseUrl(baseUrl)
            .lifecycleOwner(lifecycleOwner)
            .build()

        AccessCheckoutValidationInitialiser.initialise(
            cardValidationConfig
        )
    }

    protected fun initialiseWithAcceptedCardBrands(cardBrands: Array<CardBrands>) {
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
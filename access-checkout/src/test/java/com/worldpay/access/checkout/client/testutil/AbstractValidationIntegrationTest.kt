package com.worldpay.access.checkout.client.testutil

import android.content.Context
import android.text.method.DigitsKeyListener
import android.view.KeyEvent
import android.view.KeyEvent.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.ui.AccessEditText
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.robolectric.shadows.ShadowInstrumentation.getInstrumentation
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

open class AbstractValidationIntegrationTest {

    protected val context: Context = getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson =
        CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    protected lateinit var pan: AccessEditText
    protected lateinit var cvc: AccessEditText
    protected lateinit var expiryDate: AccessEditText

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private lateinit var server: MockWebServer

    protected lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()

    @After
    fun tearDown() {
        server.shutdown()
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
    }

    protected fun initialiseValidation(
        enablePanFormatting: Boolean = false,
        acceptedCardBrands: Array<String>? = null
    ) {
        resetValidation()

        val url = server.url(cardConfigurationEndpoint)

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .baseUrl("${url.scheme}://${url.host}:${url.port}/")
            .lifecycleOwner(lifecycleOwner)

        if (enablePanFormatting) {
            cardValidationConfig.enablePanFormatting()
        }

        if (acceptedCardBrands != null) {
            cardValidationConfig.acceptedCardBrands(acceptedCardBrands)
        }

        AccessCheckoutValidationInitialiser.initialise(
            cardValidationConfig.build()
        )
    }

    private fun resetValidation() {
        pan = AccessEditText(context)
        pan.id = 1
        pan.keyListener = DigitsKeyListener.getInstance("0123456789")
        expiryDate = AccessEditText(context)
        expiryDate.id = 2
        cvc = AccessEditText(context)
        cvc.id = 3

        cardValidationListener = spy(CardValidationListener())
        reset(cardValidationListener)

        reset(lifecycle, lifecycleOwner)
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())

        server = MockWebServer()
        server.enqueue(MockResponse().setBody(cardConfigJson))
        server.useHttps(getSslContext().socketFactory, false)
        server.start()
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

    protected fun AccessEditText.pressBackspaceAtIndex(selection: Int) {
        this.editText.setSelection(selection)
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
    }

    protected fun AccessEditText.pressBackspaceAtSelection(start: Int, end: Int) {
        this.editText.setSelection(start, end)
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
    }

    protected fun AccessEditText.typeAtIndex(selection: Int, text: String) {
        this.editText.setSelection(selection)
        this.editText.text.insert(selection, text)
//        this.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, code, 0))
//        this.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, code, 0))
    }

    protected fun AccessEditText.paste(selectionStart: Int, selectionEnd: Int, text: String) {
        this.editText.text.replace(selectionStart, selectionEnd, text)
    }
}

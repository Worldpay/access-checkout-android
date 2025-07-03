package com.worldpay.access.checkout.client.testutil

import android.content.Context
import android.os.Looper
import android.text.method.DigitsKeyListener
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_DEL
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.tomakehurst.wiremock.WireMockServer
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.cardbin.api.service.CardBinService
import com.worldpay.access.checkout.client.testutil.mocks.CardBinServiceMock
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import com.worldpay.access.checkout.validation.configuration.CardConfigurationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowInstrumentation.getInstrumentation
import java.security.KeyStore
import java.util.concurrent.TimeoutException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


@OptIn(ExperimentalCoroutinesApi::class)
open class AbstractValidationIntegrationTest {

    protected lateinit var context: Context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson =
        CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    protected lateinit var pan: AccessCheckoutEditText
    protected lateinit var cvc: AccessCheckoutEditText
    protected lateinit var expiryDate: AccessCheckoutEditText

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private lateinit var server: MockWebServer

    protected lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
    private lateinit var cardBinServer: WireMockServer

    @Before
    fun globalSetUp() {
        context = getInstrumentation().context
        cardBinServer = CardBinServiceMock.start()
        resetValidation()
        //Ensure cache is cleared before each test
        CardBinService.clearCache()
        CardConfigurationProvider.reset()
    }

    @After
    fun tearDown() {
        if (this::server.isInitialized) {
            server.shutdown()
        }

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
            .checkoutId("YOUR-CHECKOUT-ID")

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
        pan = AccessCheckoutEditText(context)
        pan.id = 1
        pan.keyListener = DigitsKeyListener.getInstance("0123456789")
        expiryDate = AccessCheckoutEditText(context)
        expiryDate.id = 2
        cvc = AccessCheckoutEditText(context)
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

    protected fun AccessCheckoutEditText.pressBackspaceAtIndex(selection: Int) {
        runAndWaitUntilIdle {
            this.editText!!.setSelection(selection)
            this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
            this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
        }
    }

    protected fun AccessCheckoutEditText.pressBackspaceAtSelection(start: Int, end: Int) {
        runAndWaitUntilIdle {
            this.editText!!.setSelection(start, end)
            this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
            this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
        }
    }

    protected fun AccessCheckoutEditText.typeAtIndex(selection: Int, text: String) {
        runAndWaitUntilIdle {
            this.editText!!.setSelection(selection)
            this.editText.text.insert(selection, text)
        }
    }

    protected fun AccessCheckoutEditText.paste(
        selectionStart: Int,
        selectionEnd: Int,
        text: String
    ) {
        this.editText!!.text.replace(selectionStart, selectionEnd, text)
    }

    fun AccessCheckoutEditText.setTextAndWait(text: String) {
        runAndWaitUntilIdle {
            this.setText(text)
        }
    }

    /**
     * Executes the given action and waits until the main looper becomes idle or the timeout is reached.
     *
     * @param timeout The maximum time to wait for the main looper to become idle, in seconds. Defaults to 5 seconds.
     * @param action The action to execute before waiting for the main looper to become idle.
     * @throws TimeoutException If the main looper does not become idle within the specified timeout.
     */
    fun runAndWaitUntilIdle(action: () -> Unit) {
        action()
        shadowOf(Looper.getMainLooper()).idle()
    }
}

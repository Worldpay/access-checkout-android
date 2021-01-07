package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.client.testutil.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.AMEX_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DINERS_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.DISCOVER_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.JCB_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MAESTRO_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.MASTERCARD_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DINERS_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.DISCOVER_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.JCB_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MAESTRO_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.MASTERCARD_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.state.CardValidationStateManager
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    private lateinit var pan: EditText
    private lateinit var cvc: EditText
    private lateinit var expiryDate: EditText

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private val toCardBrandTransformer = ToCardBrandTransformer()

    private val server = MockWebServer()

    private lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)

        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())

        server.enqueue(MockResponse().setBody(cardConfigJson))
        server.useHttps(getSslContext().socketFactory, false)
        server.start()

        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        cardValidationListener = spy(CardValidationListener())

        pan = EditText(context)
        cvc = EditText(context)
        expiryDate = EditText(context)

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .baseUrl(baseUrl)
            .lifecycleOwner(lifecycleOwner)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig)

        reset(cardValidationListener)
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

    @Test
    fun `should call listener with valid result and no brand for valid luhn pan - onPanValidated`() {
        pan.setText(VALID_UNKNOWN_LUHN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should not call listener with invalid result and no brand for invalid luhn pan - onPanValidated`() {
        pan.setText(INVALID_UNKNOWN_LUHN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should not call listener with invalid result but should notify of visa brand for partial visa pan - onPanValidated`() {
        pan.setText(PARTIAL_VISA)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(VISA_BRAND))
    }

    @Test
    fun `should not call listener with invalid result and null brand for partial unknown pan - onPanValidated`() {
        pan.setText("000")
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener, never()).onBrandChange(any())
    }

    @Test
    fun `should call listener with valid result and correct brand for identified pan - onPanValidated`() {
        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(transform(VISA_BRAND))

        reset(cardValidationListener)

        pan.setText(MASTERCARD_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(MASTERCARD_BRAND))

        reset(cardValidationListener)

        pan.setText(AMEX_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(AMEX_BRAND))

        reset(cardValidationListener)

        pan.setText(JCB_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(JCB_BRAND))

        reset(cardValidationListener)

        pan.setText(DISCOVER_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(DISCOVER_BRAND))

        reset(cardValidationListener)

        pan.setText(DINERS_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(DINERS_BRAND))

        reset(cardValidationListener)

        pan.setText(MAESTRO_PAN)
        verify(cardValidationListener, never()).onPanValidated(any())
        verify(cardValidationListener).onBrandChange(transform(MAESTRO_BRAND))

        reset(cardValidationListener)

        pan.setText("")
        verify(cardValidationListener).onPanValidated(false)
        verify(cardValidationListener).onBrandChange(null)
    }

    @Test
    fun `should call listener with invalid result for cvc when brand is detected after pan input`() {
        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(transform(VISA_BRAND))
        verify(cardValidationListener).onCvcValidated(false)
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() {
        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(transform(VISA_BRAND))

        cvc.setText("1234")
        verify(cardValidationListener).onCvcValidated(true)

        expiryDate.setText("1229")
        verify(cardValidationListener).onExpiryDateValidated(true)

        verify(cardValidationListener).onValidationSuccess()
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - pan`() {
        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(true)
        verify(cardValidationListener).onBrandChange(transform(VISA_BRAND))

        pan.requestFocus()

        if (pan.hasFocus()) {
            pan.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - cvc`() {
        cvc.setText("123")
        verify(cardValidationListener).onCvcValidated(true)

        cvc.requestFocus()

        if (cvc.hasFocus()) {
            cvc.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should not notify validation result on focus lost where notification has already been sent - expiry date`() {
        expiryDate.setText("12/99")
        verify(cardValidationListener).onExpiryDateValidated(true)

        expiryDate.requestFocus()

        if (expiryDate.hasFocus()) {
            expiryDate.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verifyNoMoreInteractions(cardValidationListener)
    }

    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - pan`() {
        pan.setText("0000")
        verifyZeroInteractions(cardValidationListener)

        pan.requestFocus()

        if (pan.hasFocus()) {
            pan.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onPanValidated(false)
    }



    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - cvc`() {
        cvc.setText("")
        verifyZeroInteractions(cardValidationListener)

        cvc.requestFocus()

        if (cvc.hasFocus()) {
            cvc.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onCvcValidated(false)
    }



    @Test
    fun `should notify validation result on focus lost where notification has not already been sent - expiry date`() {
        expiryDate.setText("01/19")
        verifyZeroInteractions(cardValidationListener)

        expiryDate.requestFocus()

        if (expiryDate.hasFocus()) {
            expiryDate.clearFocus()
        } else {
            fail("could not gain focus")
        }

        verify(cardValidationListener).onExpiryDateValidated(false)
    }

    private fun transform(remoteCardBrand: RemoteCardBrand): CardBrand? {
        return toCardBrandTransformer.transform(remoteCardBrand)
    }

    class CardValidationListener : AccessCheckoutCardValidationListener {

        override fun onCvcValidated(isValid: Boolean) {}

        override fun onValidationSuccess() {}

        override fun onPanValidated(isValid: Boolean) {}

        override fun onBrandChange(cardBrand : CardBrand?) {}

        override fun onExpiryDateValidated(isValid: Boolean) {}

    }

}

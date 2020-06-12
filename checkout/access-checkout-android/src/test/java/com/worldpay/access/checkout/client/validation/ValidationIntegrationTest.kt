package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
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
import com.worldpay.access.checkout.validation.transformers.ToCardBrandTransformer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class ValidationIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    private val pan = EditText(context)
    private val cvc = EditText(context)
    private val expiryDate = EditText(context)

    private val toCardBrandTransformer = ToCardBrandTransformer()

    private lateinit var cardValidationListener: CardValidationListener

    @Before
    fun setup() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(cardConfigJson))
        server.start()

        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        cardValidationListener = spy(CardValidationListener())

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvv(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .baseUrl(baseUrl)
            .build()

        AccessCheckoutValidationInitialiser.initialise(cardValidationConfig)
    }

    @Test
    fun `should call listener with valid result and no brand for valid luhn pan - onPanValidated`() {
        pan.setText(VALID_UNKNOWN_LUHN)
        verify(cardValidationListener).onPanValidated(null, true)
    }

    @Test
    fun `should call listener with invalid result and no brand for invalid luhn pan - onPanValidated`() {
        pan.setText(INVALID_UNKNOWN_LUHN)
        verify(cardValidationListener).onPanValidated(null, false)
    }

    @Test
    fun `should call listener with invalid result and visa brand for partial visa pan - onPanValidated`() {
        pan.setText(PARTIAL_VISA)
        verify(cardValidationListener).onPanValidated(transform(VISA_BRAND), false)
    }

    @Test
    fun `should call listener with invalid result and null brand for partial unknown pan - onPanValidated`() {
        pan.setText("000")
        verify(cardValidationListener).onPanValidated(null, false)
    }

    @Test
    fun `should call listener with valid result and correct brand for identified pan - onPanValidated`() {
        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(transform(VISA_BRAND), true)

        pan.setText(MASTERCARD_PAN)
        verify(cardValidationListener).onPanValidated(transform(MASTERCARD_BRAND), true)

        pan.setText(AMEX_PAN)
        verify(cardValidationListener).onPanValidated(transform(AMEX_BRAND), true)

        pan.setText(JCB_PAN)
        verify(cardValidationListener).onPanValidated(transform(JCB_BRAND), true)

        pan.setText(DISCOVER_PAN)
        verify(cardValidationListener).onPanValidated(transform(DISCOVER_BRAND), true)

        pan.setText(DINERS_PAN)
        verify(cardValidationListener).onPanValidated(transform(DINERS_BRAND), true)

        pan.setText(MAESTRO_PAN)
        verify(cardValidationListener).onPanValidated(transform(MAESTRO_BRAND), true)
    }

    @Test
    fun `should call listener with invalid result for cvv when brand is detected after pan input`() {
        cvc.setText("1234")
        verify(cardValidationListener).onCvvValidated(true)

        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(transform(VISA_BRAND), true)
        verify(cardValidationListener).onCvvValidated(false)
    }

    @Test
    fun `should call each listener function as each input is filled and then finally call the onValidationSuccess function`() {
        pan.setText(VISA_PAN)
        verify(cardValidationListener).onPanValidated(transform(VISA_BRAND), true)

        cvc.setText("1234")
        verify(cardValidationListener).onCvvValidated(true)

        expiryDate.setText("1229")
        verify(cardValidationListener).onExpiryDateValidated(true)

        verify(cardValidationListener).onValidationSuccess()
    }

    private fun transform(remoteCardBrand: RemoteCardBrand): CardBrand? {
        return toCardBrandTransformer.transform(remoteCardBrand)
    }

    class CardValidationListener : AccessCheckoutCardValidationListener {

        override fun onCvvValidated(isValid: Boolean) {}

        override fun onValidationSuccess() {}

        override fun onPanValidated(cardBrand: CardBrand?, isValid: Boolean) {}

        override fun onExpiryDateValidated(isValid: Boolean) {}

    }

}

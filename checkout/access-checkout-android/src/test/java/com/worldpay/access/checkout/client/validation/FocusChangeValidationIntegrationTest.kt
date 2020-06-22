package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.*
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
import kotlin.test.fail

@RunWith(RobolectricTestRunner::class)
class FocusChangeValidationIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cardConfigurationEndpoint = "/access-checkout/cardTypes.json"

    private val cardConfigJson = CardConfiguration::class.java.getResource("remote_card_config.json")?.readText()!!

    private val pan = EditText(context)
    private val cvc = EditText(context)
    private val expiryDate = EditText(context)
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    private lateinit var cardValidationListener: CardValidationListener

    @Before
    fun setup() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody(cardConfigJson))
        server.start()

        val url = server.url(cardConfigurationEndpoint)
        val baseUrl = "${url.scheme}://${url.host}:${url.port}/"

        cardValidationListener = spy(CardValidationListener())

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

    class CardValidationListener : AccessCheckoutCardValidationListener {

        override fun onCvcValidated(isValid: Boolean) {}

        override fun onValidationSuccess() {}

        override fun onPanValidated(isValid: Boolean) {}

        override fun onBrandChange(cardBrand : CardBrand?) {}

        override fun onExpiryDateValidated(isValid: Boolean) {}

    }

}

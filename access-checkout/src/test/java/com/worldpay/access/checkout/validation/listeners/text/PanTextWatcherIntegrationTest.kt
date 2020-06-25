package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVC_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PanTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvc = EditText(context)
    private val pan = EditText(context)
    private val cvcValidationRuleManager = CVCValidationRuleManager()

    private val cvcValidator = mock<CvcValidator>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()
    private val brandChangedHandler = mock<BrandChangedHandler>()

    @Before
    fun setup() {
        mockSuccessfulCardConfiguration()

        val cvcTextWatcher = CvcTextWatcher(
            cvcValidator = cvcValidator
        )

        cvc.addTextChangedListener(cvcTextWatcher)

        val panTextWatcher = PanTextWatcher(
            panValidator = PanValidator(),
            cvcValidator = cvcValidator,
            cvcEditText = cvc,
            panValidationResultHandler = panValidationResultHandler,
            brandChangedHandler = brandChangedHandler,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        pan.addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should validate pan as false when partial unknown pan is entered`() {
        pan.setText("00000")

        verify(panValidationResultHandler).handleResult(false)
        verify(brandChangedHandler, never()).handle(any())
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() {
        pan.setText(PARTIAL_VISA)

        verify(panValidationResultHandler).handleResult(false)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() {
        pan.setText(VISA_PAN)

        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() {
        pan.setText(VALID_UNKNOWN_LUHN)

        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler, never()).handle(any())
    }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() {
        pan.setText(INVALID_UNKNOWN_LUHN)

        verify(panValidationResultHandler).handleResult(false)
        verify(brandChangedHandler, never()).handle(any())
    }

    @Test
    fun `should validate cvc when pan brand is recognised and cvc is not empty`() {
        assertEquals(CVC_DEFAULTS, cvcValidationRuleManager.getRule())

        cvc.setText("123")
        reset(cvcValidator)

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvc, cvcValidationRuleManager.getRule())

        verify(cvcValidator).validate("123")
        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

    @Test
    fun `should not validate cvc when pan brand is recognised and cvc is empty`() {
        assertEquals(CVC_DEFAULTS, cvcValidationRuleManager.getRule())

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvc, cvcValidationRuleManager.getRule())

        verify(cvcValidator, never()).validate(any())
        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

}

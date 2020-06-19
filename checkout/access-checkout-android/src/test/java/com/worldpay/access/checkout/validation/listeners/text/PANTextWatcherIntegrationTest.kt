package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.INVALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VALID_UNKNOWN_LUHN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.handler.BrandChangedHandler
import com.worldpay.access.checkout.validation.result.handler.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PANTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvv = EditText(context)
    private val pan = EditText(context)
    private val cvcValidationRuleManager = CVCValidationRuleManager()

    private val cvcValidator = mock<CVCValidator>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()
    private val brandChangedHandler = mock<BrandChangedHandler>()

    @Before
    fun setup() {
        val cvvTextWatcher = CVVTextWatcher(
            cvcValidator = cvcValidator
        )

        cvv.addTextChangedListener(cvvTextWatcher)

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            cvcValidator = cvcValidator,
            cvvEditText = cvv,
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
    fun `should validate cvv when pan brand is recognised and cvv is not empty`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        cvv.setText("123")
        reset(cvcValidator)

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())

        verify(cvcValidator).validate("123")
        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

    @Test
    fun `should not validate cvv when pan brand is recognised and cvv is empty`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())

        verify(cvcValidator, never()).validate(any())
        verify(panValidationResultHandler).handleResult(true)
        verify(brandChangedHandler).handle(VISA_BRAND)
    }

}

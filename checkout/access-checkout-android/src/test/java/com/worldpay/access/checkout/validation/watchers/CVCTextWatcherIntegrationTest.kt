package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class CVCTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvv = EditText(context)
    private val pan = EditText(context)

    private lateinit var cvvValidationResultHandler: CvvValidationResultHandler

    @Before
    fun setup() {
        cvvValidationResultHandler = mock()

        val cvcValidationRuleManager = CVCValidationRuleManager()
        val cvcValidator = CVCValidator(cvvValidationResultHandler, cvcValidationRuleManager)

        val cvvTextWatcher = CVVTextWatcher(
            cvcValidator = cvcValidator
        )

        cvv.addTextChangedListener(cvvTextWatcher)

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            cvcValidator = cvcValidator,
            cvvEditText = cvv,
            panValidationResultHandler = mock(),
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        pan.addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should validate cvv as false given 1 digit cvv is entered and no pan`() {
        cvv.setText("1")

        verify(cvvValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvv as false given 2 digit cvv is entered and no pan`() {
        cvv.setText("12")

        verify(cvvValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvv as true given 3 digit cvv is entered and no pan`() {
        cvv.setText("123")

        verify(cvvValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvv as true given 4 digit cvv is entered and no pan`() {
        cvv.setText("1234")

        verify(cvvValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvv as false given 5 digit cvv is entered and no pan`() {
        cvv.setText("12345")

        verify(cvvValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvv as true given 3 digit cvv is entered and visa pan is entered`() {
        pan.setText(VISA_PAN)
        cvv.setText("123")

        verify(cvvValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvv as false given 4 digit cvv is entered and visa pan is entered`() {
        pan.setText(VISA_PAN)
        cvv.setText("1234")

        verify(cvvValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvv as true given 4 digit cvv is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvv.setText("1234")

        verify(cvvValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvv as false given 5 digit cvv is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvv.setText("12345")

        verify(cvvValidationResultHandler).handleResult(false)
    }

}

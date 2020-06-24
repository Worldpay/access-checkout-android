package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.mockSuccessfulCardConfiguration
import com.worldpay.access.checkout.testutils.CardNumberUtil.AMEX_PAN
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import com.worldpay.access.checkout.validation.validators.PanValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation

@RunWith(RobolectricTestRunner::class)
class CvcTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvc = EditText(context)
    private val pan = EditText(context)

    private lateinit var cvcValidationResultHandler: CvcValidationResultHandler

    @Before
    fun setup() {
        mockSuccessfulCardConfiguration()

        cvcValidationResultHandler = mock()

        val cvcValidationRuleManager = CVCValidationRuleManager()
        val cvcValidator = CvcValidator(cvcValidationResultHandler, cvcValidationRuleManager)

        val cvcTextWatcher = CvcTextWatcher(
            cvcValidator = cvcValidator
        )

        cvc.addTextChangedListener(cvcTextWatcher)

        val panTextWatcher = PanTextWatcher(
            panValidator = PanValidator(),
            cvcValidator = cvcValidator,
            cvcEditText = cvc,
            panValidationResultHandler = mock(),
            brandChangedHandler = mock(),
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        pan.addTextChangedListener(panTextWatcher)
    }

    @Test
    fun `should validate cvc as false given 1 digit cvc is entered and no pan`() {
        cvc.setText("1")

        verify(cvcValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvc as false given 2 digit cvc is entered and no pan`() {
        cvc.setText("12")

        verify(cvcValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and no pan`() {
        cvc.setText("123")

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and no pan`() {
        cvc.setText("1234")

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvc as false given 5 digit cvc is entered and no pan`() {
        cvc.setText("12345")

        verify(cvcValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvc as true given 3 digit cvc is entered and visa pan is entered`() {
        pan.setText(VISA_PAN)
        cvc.setText("123")

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvc as false given 4 digit cvc is entered and visa pan is entered`() {
        pan.setText(VISA_PAN)
        cvc.setText("1234")

        verify(cvcValidationResultHandler).handleResult(false)
    }

    @Test
    fun `should validate cvc as true given 4 digit cvc is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvc.setText("1234")

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should validate cvc as false given 5 digit cvc is entered and amex pan is entered`() {
        pan.setText(AMEX_PAN)
        cvc.setText("12345")

        verify(cvcValidationResultHandler).handleResult(false)
    }

}

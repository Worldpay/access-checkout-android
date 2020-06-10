package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.CVVValidator
import org.junit.Before
import org.junit.Test

class CVVTextWatcherTest {

    private val cvvValidationResultHandler = mock<CvvValidationResultHandler>()
    private val cvvEditable = mock<Editable>()

    private lateinit var cvvTextWatcher: CVVTextWatcher
    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()

        val cvcValidator = CVCValidator(
            cvvValidationResultHandler = cvvValidationResultHandler,
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        cvvTextWatcher = CVVTextWatcher(cvcValidator)
    }

    @Test
    fun `should handle valid result after text changes`() {
        given(cvvEditable.toString()).willReturn("123")

        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(cvvValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cvvValidator = mock<CVVValidator>()

        cvvTextWatcher.beforeTextChanged("", 1, 2,3)
        cvvTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cvvValidator,
            cvvValidationResultHandler
        )
    }

}

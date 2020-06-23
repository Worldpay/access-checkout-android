package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import org.junit.Before
import org.junit.Test

class CVCTextWatcherTest {

    private val cvcValidationResultHandler = mock<CvcValidationResultHandler>()
    private val cvcEditable = mock<Editable>()

    private lateinit var cvcTextWatcher: CVCTextWatcher
    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()

        val cvcValidator = CVCValidator(
            cvcValidationResultHandler = cvcValidationResultHandler,
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        cvcTextWatcher = CVCTextWatcher(cvcValidator)
    }

    @Test
    fun `should handle valid result after text changes`() {
        given(cvcEditable.toString()).willReturn("123")

        cvcTextWatcher.afterTextChanged(cvcEditable)

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cvcValidator = mock<CVCValidator>()

        cvcTextWatcher.beforeTextChanged("", 1, 2,3)
        cvcTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cvcValidator,
            cvcValidationResultHandler
        )
    }

}

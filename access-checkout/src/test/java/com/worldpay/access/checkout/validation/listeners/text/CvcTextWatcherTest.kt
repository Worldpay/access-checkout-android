package com.worldpay.access.checkout.validation.listeners.text

import android.text.Editable
import com.worldpay.access.checkout.validation.result.handler.CvcValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CvcValidator
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class CvcTextWatcherTest {

    private val cvcValidationResultHandler = mock<CvcValidationResultHandler>()
    private val cvcEditable = mock<Editable>()

    private lateinit var cvcTextWatcher: CvcTextWatcher
    private lateinit var cvcValidationRuleManager: CVCValidationRuleManager

    @Before
    fun setup() {
        cvcValidationRuleManager = CVCValidationRuleManager()

        val cvcValidator = CvcValidator(
            cvcValidationResultHandler = cvcValidationResultHandler,
            cardValidationRuleProvider = cvcValidationRuleManager
        )

        cvcTextWatcher = CvcTextWatcher(cvcValidator)
    }

    @Test
    fun `should handle valid result after text changes`() {
        given(cvcEditable.toString()).willReturn("123")

        cvcTextWatcher.afterTextChanged(cvcEditable)

        verify(cvcValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cvcValidator = mock<CvcValidator>()

        cvcTextWatcher.beforeTextChanged("", 1, 2, 3)
        cvcTextWatcher.onTextChanged("", 1, 2, 3)

        verifyNoInteractions(
            cvcValidator,
            cvcValidationResultHandler
        )
    }
}

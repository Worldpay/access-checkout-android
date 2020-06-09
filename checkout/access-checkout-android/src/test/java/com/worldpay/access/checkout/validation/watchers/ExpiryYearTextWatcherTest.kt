package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.result.ExpiryYearValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator
import org.junit.Before
import org.junit.Test

class ExpiryYearTextWatcherTest {

    private val expiryYearValidationResultHandler = mock<ExpiryYearValidationResultHandler>()

    private val yearEditable = mock<Editable>()

    private lateinit var expiryYearTextWatcher: ExpiryYearTextWatcher

    @Before
    fun setup() {
        expiryYearTextWatcher = ExpiryYearTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = DateValidator(),
            expiryYearValidationResultHandler = expiryYearValidationResultHandler
        )
    }

    @Test
    fun `should pass validation rule to validation rule handler`() {
        given(yearEditable.toString()).willReturn("02")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(expiryYearValidationResultHandler).handleResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun `should pass validation result to validation result handler - valid case`() {
        given(yearEditable.toString()).willReturn("29")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(expiryYearValidationResultHandler).handleResult(ValidationResult(partial = false, complete = true))
    }

    @Test
    fun `should pass validation result to validation result handler - invalid case`() {
        given(yearEditable.toString()).willReturn("abc")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(expiryYearValidationResultHandler).handleResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val dateValidator = mock<DateValidator>()

        val expiryYearTextWatcher = ExpiryYearTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = dateValidator,
            expiryYearValidationResultHandler = expiryYearValidationResultHandler
        )

        expiryYearTextWatcher.beforeTextChanged("", 1, 2,3)
        expiryYearTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            dateValidator,
            expiryYearValidationResultHandler
        )
    }

}

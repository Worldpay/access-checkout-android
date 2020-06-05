package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_MONTH_RULE
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.result.ValidationResultHandler
import com.worldpay.access.checkout.validation.validators.DateValidator
import org.junit.Before
import org.junit.Test

class ExpiryMonthTextWatcherTest {

    private val inputFilterHandler = mock<InputFilterHandler>()
    private val validationResultHandler = mock<ValidationResultHandler>()

    private val expiryMonthEditText = mock<EditText>()
    private val monthEditable = mock<Editable>()

    private lateinit var expiryMonthTextWatcher: ExpiryMonthTextWatcher

    @Before
    fun setup() {
        expiryMonthTextWatcher = ExpiryMonthTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = DateValidator(),
            expiryMonthEditText = expiryMonthEditText,
            validationResultHandler = validationResultHandler,
            inputFilterHandler = inputFilterHandler
        )
    }

    @Test
    fun `should pass validation rule to validation rule handler`() {
        given(monthEditable.toString()).willReturn("02")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verify(inputFilterHandler).handle(expiryMonthEditText, EXP_MONTH_RULE)
    }

    @Test
    fun `should pass validation result to validation result handler - valid case`() {
        given(monthEditable.toString()).willReturn("02")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verify(validationResultHandler).handleExpiryMonthValidationResult(ValidationResult(partial = false, complete = true))
    }

    @Test
    fun `should pass validation result to validation result handler - invalid case`() {
        given(monthEditable.toString()).willReturn("022")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verify(validationResultHandler).handleExpiryMonthValidationResult(ValidationResult(partial = false, complete = false))
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val dateValidator = mock<DateValidator>()

        val expiryMonthTextWatcher = ExpiryMonthTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = dateValidator,
            expiryMonthEditText = expiryMonthEditText,
            validationResultHandler = validationResultHandler
        )

        expiryMonthTextWatcher.beforeTextChanged("", 1, 2,3)
        expiryMonthTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            dateValidator,
            validationResultHandler,
            inputFilterHandler
        )
    }

}

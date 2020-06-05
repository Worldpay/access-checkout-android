package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Defaults.EXP_YEAR_RULE
import com.worldpay.access.checkout.validation.InputFilterHandler
import com.worldpay.access.checkout.validation.ValidationResult
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.EXPIRY_YEAR
import com.worldpay.access.checkout.validation.validators.DateValidator
import org.junit.Before
import org.junit.Test

class ExpiryYearTextWatcherTest {

    private val inputFilterHandler = mock<InputFilterHandler>()
    private val validationResultHandler = mock<ValidationResultHandler>()

    private val expiryYearEditText = mock<EditText>()
    private val yearEditable = mock<Editable>()

    private lateinit var expiryYearTextWatcher: ExpiryYearTextWatcher

    @Before
    fun setup() {
        expiryYearTextWatcher = ExpiryYearTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = DateValidator(),
            inputFilterHandler = inputFilterHandler,
            validationResultHandler = validationResultHandler,
            expiryYearEditText = expiryYearEditText
        )
    }

    @Test
    fun `should pass validation rule to validation rule handler`() {
        given(yearEditable.toString()).willReturn("02")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(inputFilterHandler).handle(expiryYearEditText, EXP_YEAR_RULE)
    }

    @Test
    fun `should pass validation result to validation result handler - valid case`() {
        given(yearEditable.toString()).willReturn("29")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(validationResultHandler).handle(EXPIRY_YEAR, ValidationResult(partial = false, complete = true))
    }

    @Test
    fun `should pass validation result to validation result handler - invalid case`() {
        given(yearEditable.toString()).willReturn("abc")

        expiryYearTextWatcher.afterTextChanged(yearEditable)

        verify(validationResultHandler).handle(EXPIRY_YEAR, ValidationResult(partial = false, complete = false))
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cardDetailComponents = mock<CardDetailComponents>()
        val dateValidator = mock<DateValidator>()

        val expiryYearTextWatcher = ExpiryYearTextWatcher(
            cardConfiguration = CARD_CONFIG_BASIC,
            dateValidator = dateValidator,
            inputFilterHandler = inputFilterHandler,
            validationResultHandler = validationResultHandler,
            expiryYearEditText = expiryYearEditText
        )

        expiryYearTextWatcher.beforeTextChanged("", 1, 2,3)
        expiryYearTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cardDetailComponents,
            dateValidator,
            validationResultHandler,
            inputFilterHandler
        )
    }

}

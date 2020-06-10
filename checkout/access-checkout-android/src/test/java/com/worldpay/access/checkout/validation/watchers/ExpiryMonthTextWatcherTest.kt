package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator
import org.junit.Before
import org.junit.Test

class ExpiryMonthTextWatcherTest {

    private val expiryDateValidationResultHandler = mock<ExpiryDateValidationResultHandler>()
    private val dateValidator = spy(NewDateValidator())

    private val yearEditable = mock<Editable>()
    private val monthEditable = mock<Editable>()

    private lateinit var expiryMonthTextWatcher: ExpiryMonthTextWatcher

    @Before
    fun setup() {
        val yearEditText = mock<EditText>()
        given(yearEditText.text).willReturn(yearEditable)

        expiryMonthTextWatcher = ExpiryMonthTextWatcher(
            dateValidator = dateValidator,
            yearEditText = yearEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )
    }

    @Test
    fun `should not validate when year is empty`() {
        given(monthEditable.toString()).willReturn("02")
        given(yearEditable.toString()).willReturn("")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verifyZeroInteractions(dateValidator, expiryDateValidationResultHandler)
    }

    @Test
    fun `should pass the month and year into the date validator`() {
        given(monthEditable.toString()).willReturn("02")
        given(yearEditable.toString()).willReturn("29")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verify(dateValidator).validate("02", "29")
    }

    @Test
    fun `should handle validation result once one is retrieved`() {
        given(monthEditable.toString()).willReturn("02")
        given(yearEditable.toString()).willReturn("29")

        expiryMonthTextWatcher.afterTextChanged(monthEditable)

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        expiryMonthTextWatcher.beforeTextChanged("", 1, 2,3)
        expiryMonthTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            dateValidator,
            expiryDateValidationResultHandler
        )
    }

}

package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.validation.result.ExpiryDateValidationResultHandler
import com.worldpay.access.checkout.validation.validators.NewDateValidator
import org.junit.Before
import org.junit.Test

class ExpiryDateTextWatcherTest {

    private val expiryDateValidationResultHandler = mock<ExpiryDateValidationResultHandler>()
    private val dateValidator = spy(NewDateValidator())

    private val expiryDateEditable = mock<Editable>()

    private lateinit var expiryDateTextWatcher: ExpiryDateTextWatcher

    @Before
    fun setup() {
        val expiryDateEditText = mock<EditText>()
        given(expiryDateEditText.text).willReturn(expiryDateEditable)

        expiryDateTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )
    }

    @Test
    fun `should not validate when month is empty`() {
        given(expiryDateEditable.toString()).willReturn("29")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verifyZeroInteractions(dateValidator, expiryDateValidationResultHandler)
    }

    @Test
    fun `should pass the month and year into the date validator`() {
        given(expiryDateEditable.toString()).willReturn("02/29")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("02", "29")
    }

    @Test
    fun `should handle validation result once one is retrieved`() {
        given(expiryDateEditable.toString()).willReturn("02/29")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(expiryDateValidationResultHandler).handleResult(true)
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        expiryDateTextWatcher.beforeTextChanged("", 1, 2,3)
        expiryDateTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            dateValidator,
            expiryDateValidationResultHandler
        )
    }

}

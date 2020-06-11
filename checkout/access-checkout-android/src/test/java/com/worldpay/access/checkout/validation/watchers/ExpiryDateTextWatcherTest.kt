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
    private val expiryDateEditText = mock<EditText>()

    private lateinit var expiryDateTextWatcher: ExpiryDateTextWatcher

    @Before
    fun setup() {
        given(expiryDateEditText.text).willReturn(expiryDateEditable)

        expiryDateTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDateEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler
        )
    }

    @Test
    fun `should not validate with 1 character entered`() {
        given(expiryDateEditable.toString()).willReturn("0")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("0")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(expiryDateEditText)
    }

    @Test
    fun `should not validate with 2 character entered but will add forward slash`() {
        given(expiryDateEditable.toString()).willReturn("02")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(expiryDateEditText).setText("02/")
        verifyZeroInteractions(dateValidator, expiryDateValidationResultHandler)
    }

    @Test
    fun `should not validate with 4 character entered`() {
        given(expiryDateEditable.toString()).willReturn("02/2")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("02/2")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(expiryDateEditText)
    }

    @Test
    fun `should validate with 5 character entered`() {
        given(expiryDateEditable.toString()).willReturn("02/29")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("02/29")
        verify(expiryDateValidationResultHandler).handleResult(true)
        verifyZeroInteractions(expiryDateEditText)
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

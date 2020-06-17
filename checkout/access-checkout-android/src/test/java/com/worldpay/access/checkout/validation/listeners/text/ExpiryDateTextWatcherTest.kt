package com.worldpay.access.checkout.validation.listeners.text

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
    private val dateSanitiser = spy<ExpiryDateSanitiser>()

    private val expiryDateEditable = mock<Editable>()
    private val expiryDateEditText = mock<EditText>()

    private lateinit var expiryDateTextWatcher: ExpiryDateTextWatcher

    @Before
    fun setup() {
        given(expiryDateEditText.text).willReturn(expiryDateEditable)

        expiryDateTextWatcher = ExpiryDateTextWatcher(
            dateValidator = dateValidator,
            expiryDateEditText = expiryDateEditText,
            expiryDateValidationResultHandler = expiryDateValidationResultHandler,
            expiryDateSanitiser = dateSanitiser
        )
    }

    @Test
    fun `should attempt to format text when deleting characters`() {
        expiryDateTextWatcher.beforeTextChanged("13",0,0,0)
        given(expiryDateEditable.toString()).willReturn("1")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateSanitiser).sanitise("1")
        verify(dateValidator).validate("1")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(expiryDateEditText)
    }

    @Test
    fun `should not attempt to format text when deleting separator characters`() {
        expiryDateTextWatcher.beforeTextChanged("13/",0,0,0)
        given(expiryDateEditable.toString()).willReturn("13")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("13")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(dateSanitiser, expiryDateEditText)
    }

    @Test
    fun `should sanitise input when expiry date is entered`() {
        given(expiryDateEditable.toString()).willReturn("0")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateSanitiser).sanitise("0")
        verify(dateValidator).validate("0")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(expiryDateEditText)
    }

    @Test
    fun `should reset text value and cursor when expiry date is santised`() {
        given(expiryDateEditable.toString()).willReturn("13")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateSanitiser).sanitise("13")
        verify(expiryDateEditText).setText("01/3")
        verify(expiryDateEditText).setSelection(4)
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
    fun `should validate with empty string entered`() {
        given(expiryDateEditable.toString()).willReturn("")

        expiryDateTextWatcher.afterTextChanged(expiryDateEditable)

        verify(dateValidator).validate("")
        verify(expiryDateValidationResultHandler).handleResult(false)
        verifyZeroInteractions(dateSanitiser, expiryDateEditText)
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

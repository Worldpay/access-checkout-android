package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVVValidator
import org.junit.Before
import org.junit.Test

class CVVTextWatcherTest {

    private val cvvValidationResultHandler = mock<CvvValidationResultHandler>()

    private val cvcValidationHandler = mock<CVCValidationHandler>()

    private val cvvEditable = mock<Editable>()

    private lateinit var cvvTextWatcher: CVVTextWatcher

    @Before
    fun setup() {
        cvvTextWatcher = CVVTextWatcher(
            cvcValidationHandler = cvcValidationHandler
        )

        given(cvvEditable.toString()).willReturn("123")
    }

    @Test
    fun `should call validation handler with cvv after text changed`() {
        cvvTextWatcher.afterTextChanged(cvvEditable)

        verify(cvcValidationHandler).validate(cvvEditable.toString())
    }

    @Test
    fun `should do nothing when beforeTextChanged or onTextChanged is called`() {
        val cvvValidator = mock<CVVValidator>()

        cvvTextWatcher = CVVTextWatcher(
            cvcValidationHandler = cvcValidationHandler
        )

        cvvTextWatcher.beforeTextChanged("", 1, 2,3)
        cvvTextWatcher.onTextChanged("", 1, 2,3)

        verifyZeroInteractions(
            cvvValidator,
            cvvValidationResultHandler
        )
    }
}

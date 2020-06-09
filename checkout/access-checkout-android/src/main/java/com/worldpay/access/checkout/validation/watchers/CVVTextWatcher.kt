package com.worldpay.access.checkout.validation.watchers

import android.text.Editable
import android.widget.EditText
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.result.CvvValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVVValidator

internal class CVVTextWatcher(
    private val cardConfiguration: CardConfiguration,
    private val panEditText: EditText?,
    private val cvvValidator: CVVValidator,
    private val cvvValidationResultHandler: CvvValidationResultHandler
): AbstractCardDetailTextWatcher() {

    override fun afterTextChanged(cvv: Editable?) {
        val pan = getPan()

        val result = cvvValidator.validate(cvv.toString(), pan, cardConfiguration)
        cvvValidationResultHandler.handleResult(
            validationResult = result.first
        )
    }

    private fun getPan(): String {
        if (panEditText == null) {
            return ""
        }
        return panEditText.text.toString()
    }

}

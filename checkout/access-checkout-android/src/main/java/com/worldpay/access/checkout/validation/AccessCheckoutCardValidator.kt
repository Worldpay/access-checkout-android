package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardConfiguration
import java.util.*

/**
 * Implementation of a [CardValidator] which delegates responsibility of validating the card fields to the individual
 * card validator implementations
 */
class AccessCheckoutCardValidator(override val cardConfiguration: CardConfiguration) : CardValidator {

    private val panValidator: PANValidator
    private val cvvValidator: CVVValidator
    private val dateValidator: DateValidator

    init {
        this.panValidator = PANValidatorImpl(cardConfiguration)
        this.cvvValidator = CVVValidatorImpl(cardConfiguration)
        this.dateValidator = DateValidatorImpl(Calendar.getInstance(), cardConfiguration)
    }

    override fun validatePAN(pan: PAN): Pair<ValidationResult, CardBrand?> = panValidator.validate(pan)

    override fun validateCVV(cvv: CVV, pan: PAN?): Pair<ValidationResult, CardBrand?> = cvvValidator.validate(cvv, pan)

    override fun validateDate(month: Month?, year: Year?): ValidationResult = dateValidator.validate(month, year)

    override fun canUpdate(month: Month?, year: Year?): Boolean = dateValidator.canUpdate(month, year)
}
package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.*

/**
 * Implementation of a [CardValidator] which delegates responsibility of validating the card fields to the individual
 * card validator implementations
 */
class AccessCheckoutCardValidator @JvmOverloads constructor(override val cardConfiguration: CardConfiguration? = null,
                                                            private val panValidator: PANValidator = PANValidator(),
                                                            private val cvvValidator: CVVValidator = CVVValidator(),
                                                            private val dateValidator: DateValidator = DateValidator()
) : CardValidator {

    override fun validatePAN(pan: String): Pair<ValidationResult, RemoteCardBrand?> = panValidator.validate(pan, getCardConfig())

    override fun validateCVV(cvv: CVV, pan: String?): Pair<ValidationResult, RemoteCardBrand?> = cvvValidator.validate(cvv, pan, getCardConfig())

    override fun validateDate(month: Month?, year: Year?): ValidationResult = dateValidator.validate(month, year, getCardConfig())

    override fun canUpdate(month: Month?, year: Year?): Boolean = dateValidator.canUpdate(month, year, getCardConfig())

    private fun getCardConfig() : CardConfiguration {
        return cardConfiguration ?: CardConfiguration(emptyList(), CARD_DEFAULTS)
    }

}

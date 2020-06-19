package com.worldpay.access.checkout.client.api.exception

import com.worldpay.access.checkout.api.exception.ValidationRule

/**
 * General Exception class for any errors that occur within the Access Checkout SDK
 *
 * @property[message] the error message
 * @property[cause] any nested exceptions
 * @property[validationRules] any validation rules that were erred upon request
 */
data class AccessCheckoutException(
    override val message : String,
    override val cause : Exception? = null,
    val validationRules : List<ValidationRule> = emptyList()
) : RuntimeException()

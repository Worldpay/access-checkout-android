package com.worldpay.access.checkout.client.api.exception

/**
 * General Exception class for any errors that occur within the Access Checkout SDK
 *
 * @property[message] the error message
 * @property[cause] any nested exceptions
 * @property[validationRules] any validation rules that were erred upon request
 */
data class AccessCheckoutException(
    override val message: String,
    override val cause: Exception? = null,
    val validationRules: List<ValidationRule> = emptyList()
) : RuntimeException()

/**
 * Validation rule object that represents a response from the api
 *
 * @property[errorName] the name of the error
 * @property[message] the error message
 * @property[jsonPath] the json path to the error point
 */
data class ValidationRule(val errorName: String, val message: String, val jsonPath: String)

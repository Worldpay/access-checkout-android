package com.worldpay.access.checkout.client.api.exception

/**
 * General Exception class for any errors that occur within the Access Checkout SDK
 *
 * @property[errorCode] the error code
 * @property[cause] any nested exceptions
 * @property[validationRules] any validation rules that were erred upon request
 */
data class ClientErrorException (
    override val cause: Exception? = null,
    val errorCode: Int,
    val validationRules: List<ValidationRule> = emptyList()
) : RuntimeException()

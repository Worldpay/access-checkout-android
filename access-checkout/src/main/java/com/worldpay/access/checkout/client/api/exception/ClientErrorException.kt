package com.worldpay.access.checkout.client.api.exception

/**
 * General Exception class for any errors that occur within the Access Checkout SDK
 *
 * @property[cause] any nested exceptions
 * @property[errorCode] the error code
 */
data class ClientErrorException (
    val errorCode: Int,
    override val cause: Exception? = null,
) : RuntimeException()

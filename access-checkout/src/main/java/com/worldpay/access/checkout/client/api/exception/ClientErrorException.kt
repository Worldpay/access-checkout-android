package com.worldpay.access.checkout.client.api.exception

/**
 * Exception class for any client errors (4xx) that occur when sending a request to a service
 *
 * @property[cause] any nested exceptions
 * @property[errorCode] the error code
 */
data class ClientErrorException (
    val errorCode: Int,
    override val cause: Exception? = null,
) : RuntimeException()

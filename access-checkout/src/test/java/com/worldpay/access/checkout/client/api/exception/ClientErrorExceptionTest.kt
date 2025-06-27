package com.worldpay.access.checkout.client.api.exception

import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class ClientErrorExceptionTest {

    @Test
    fun `should have null cause by default`() {
        val exception = ClientErrorException(400)

        assertEquals(400, exception.errorCode)
        assertNull(exception.cause)
    }

    @Test
    fun `should be able to retrieve cause from the exception`() {
        val cause = RuntimeException("some message")
        val exception = ClientErrorException(
            errorCode = 400,
            cause = cause
        )

        assertEquals(400, exception.errorCode)
        assertEquals(cause, exception.cause)
    }
}

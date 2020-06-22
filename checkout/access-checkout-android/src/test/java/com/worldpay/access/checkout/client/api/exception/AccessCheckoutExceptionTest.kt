package com.worldpay.access.checkout.client.api.exception

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccessCheckoutExceptionTest {

    @Test
    fun `should have empty validation rules by default and null cause`() {
        val exception = AccessCheckoutException("message")

        assertEquals("message", exception.message)
        assertEquals(0, exception.validationRules.size)
        assertNull(exception.cause)
    }

    @Test
    fun `should be able to retrieve validation rules from the exception`() {
        val validationRule = ValidationRule("error name", "some message", "json path")
        val exception = AccessCheckoutException(
            message = "message",
            validationRules = listOf(validationRule)
        )

        assertEquals("message", exception.message)
        assertEquals(1, exception.validationRules.size)
        assertEquals("error name", exception.validationRules[0].errorName)
        assertEquals("some message", exception.validationRules[0].message)
        assertEquals("json path", exception.validationRules[0].jsonPath)
    }

    @Test
    fun `should be able to retrieve cause from the exception`() {
        val cause = RuntimeException("some message")
        val exception = AccessCheckoutException(
            message = "message",
            cause = cause
        )

        assertEquals("message", exception.message)
        assertEquals(0, exception.validationRules.size)
        assertEquals(cause, exception.cause)
    }

}

package com.worldpay.access.checkout.validation.state

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvcValidationStateManagerTest {
    val validationStateManager =
        CvcValidationStateManager()

    @Test
    fun `should start state as invalid`() {
        assertFalse(validationStateManager.isAllValid())
        assertFalse(validationStateManager.cvvValidated)
    }

    @Test
    fun `should return true when cvv is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvvValidated = true

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when cvv is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvvValidated = false

        assertFalse(validationStateManager.isAllValid())
    }
}
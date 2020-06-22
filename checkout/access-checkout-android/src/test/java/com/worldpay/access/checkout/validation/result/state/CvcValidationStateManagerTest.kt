package com.worldpay.access.checkout.validation.result.state

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvcValidationStateManagerTest {

    private val validationStateManager = CvcValidationStateManager()

    @Test
    fun `should start state as invalid`() {
        assertFalse(validationStateManager.isAllValid())
        assertFalse(validationStateManager.cvcValidationState)
    }

    @Test
    fun `should return true when cvc is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvcValidationState = true

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when cvc is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.cvcValidationState = false

        assertFalse(validationStateManager.isAllValid())
    }
}

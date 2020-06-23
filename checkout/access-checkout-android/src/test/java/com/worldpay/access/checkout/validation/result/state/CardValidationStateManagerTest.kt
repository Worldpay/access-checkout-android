package com.worldpay.access.checkout.validation.result.state

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardValidationStateManagerTest {

    private val validationStateManager =
        CardValidationStateManager

    @Test
    fun `should return true when all is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidationState.validationState = true
        validationStateManager.expiryDateValidationState.validationState = true
        validationStateManager.cvcValidationState.validationState = true

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and pan is invalid`() {
        validationStateManager.panValidationState.validationState = false
        validationStateManager.expiryDateValidationState.validationState = true
        validationStateManager.cvcValidationState.validationState = true

        assertFalse(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and cvc is invalid`() {
        validationStateManager.panValidationState.validationState = true
        validationStateManager.expiryDateValidationState.validationState = true
        validationStateManager.cvcValidationState.validationState = false

        assertFalse(validationStateManager.isAllValid())
    }

}

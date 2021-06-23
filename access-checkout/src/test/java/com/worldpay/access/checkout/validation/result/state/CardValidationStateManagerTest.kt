package com.worldpay.access.checkout.validation.result.state

import com.nhaarman.mockitokotlin2.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class CardValidationStateManagerTest {

    private val validationStateManager = CardValidationStateManager(mock(), mock(), mock())

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

    @Test
    fun `should return false when isAllValid and expiry date is invalid`() {
        validationStateManager.panValidationState.validationState = true
        validationStateManager.expiryDateValidationState.validationState = false
        validationStateManager.cvcValidationState.validationState = true

        assertFalse(validationStateManager.isAllValid())
    }
}

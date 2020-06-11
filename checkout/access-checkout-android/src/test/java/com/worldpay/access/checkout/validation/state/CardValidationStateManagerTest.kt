package com.worldpay.access.checkout.validation.state

import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardValidationStateManagerTest {

    private val validationStateManager =
        CardValidationStateManager()

    @Test
    fun `should start state as invalid`() {
        assertFalse(validationStateManager.isAllValid())
        assertFalse(validationStateManager.panValidated)
        assertFalse(validationStateManager.expiryDateValidated)
        assertFalse(validationStateManager.cvvValidated)
    }

    @Test
    fun `should return true when all is valid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated = true
        validationStateManager.expiryDateValidated = true
        validationStateManager.cvvValidated = true

        assertTrue(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and pan is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated = false
        validationStateManager.expiryDateValidated = true
        validationStateManager.cvvValidated = true

        assertFalse(validationStateManager.isAllValid())
    }

    @Test
    fun `should return false when isAllValid and cvv is invalid`() {
        assertFalse(validationStateManager.isAllValid())

        validationStateManager.panValidated = true
        validationStateManager.expiryDateValidated = true
        validationStateManager.cvvValidated = false

        assertFalse(validationStateManager.isAllValid())
    }

}

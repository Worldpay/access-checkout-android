package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CvvValidationConfigBuilderTest {

    private val cvv = mock<EditText>()
    private val validationListener = mock<AccessCheckoutCvvValidationListener>()

    @Test
    fun `should build card validation config`() {
        val config = CvvValidationConfig.Builder()
            .cvv(cvv)
            .validationListener(validationListener)
            .build()

        assertNotNull(config)
        assertEquals(cvv, config.cvv)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where cvv is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CvvValidationConfig.Builder()
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected cvv component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CvvValidationConfig.Builder()
                .cvv(cvv)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }

}
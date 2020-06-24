package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CvcValidationConfigBuilderTest {

    private val cvc = mock<EditText>()
    private val validationListener = mock<AccessCheckoutCvcValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Test
    fun `should build card validation config`() {
        val config = CvcValidationConfig.Builder()
            .cvc(cvc)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertNotNull(config)
        assertEquals(cvc, config.cvc)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where cvc is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CvcValidationConfig.Builder()
                .validationListener(validationListener)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected cvc component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where lifecycle owner is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CvcValidationConfig.Builder()
                .cvc(cvc)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            CvcValidationConfig.Builder()
                .cvc(cvc)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }

}

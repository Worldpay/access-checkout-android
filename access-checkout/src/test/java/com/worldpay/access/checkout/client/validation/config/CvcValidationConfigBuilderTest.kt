package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class CvcValidationConfigBuilderTest {

    private val cvc = mock<AccessCheckoutEditText>()
    private val cvcInternalEditText = mock<EditText>()

    private val validationListener = mock<AccessCheckoutCvcValidationListener>()

    @Before
    fun setUp() {
        given(cvc.editText).willReturn(cvcInternalEditText)
    }

    @Test
    fun `should build card validation config`() {
        val config = CvcValidationConfig.Builder()
            .cvc(cvc)
            .validationListener(validationListener)
            .build()

        assertNotNull(config)
        assertEquals(cvcInternalEditText, config.cvc)
        assertEquals(validationListener, config.validationListener)
    }

    @Test
    fun `should throw exception where cvc is not provided`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            CvcValidationConfig.Builder()
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected cvc component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            CvcValidationConfig.Builder()
                .cvc(cvc)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }
}

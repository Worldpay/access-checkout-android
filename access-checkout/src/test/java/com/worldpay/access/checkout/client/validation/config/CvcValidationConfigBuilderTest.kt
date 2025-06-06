package com.worldpay.access.checkout.client.validation.config

import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock

class CvcValidationConfigBuilderTest {

    private val cvc = mock<AccessCheckoutEditText>()
    private val cvcInternalEditText = mock<EditText>()

    private val validationListener = mock<AccessCheckoutCvcValidationListener>()
    private val lifecycleOwner = mock<LifecycleOwner>()

    @Before
    fun setUp() {
        given(cvc.editText).willReturn(cvcInternalEditText)
    }

    @Test
    fun `should build card validation config`() {
        val config = CvcValidationConfig.Builder()
            .cvc(cvc)
            .validationListener(validationListener)
            .lifecycleOwner(lifecycleOwner)
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
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected cvc component to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where lifecycle owner is not provided`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            CvcValidationConfig.Builder()
                .cvc(cvc)
                .validationListener(validationListener)
                .build()
        }

        assertEquals("Expected lifecycle owner to be provided but was not", exception.message)
    }

    @Test
    fun `should throw exception where validation listener is not provided`() {
        val exception = assertFailsWith<AccessCheckoutException> {
            CvcValidationConfig.Builder()
                .cvc(cvc)
                .lifecycleOwner(lifecycleOwner)
                .build()
        }

        assertEquals("Expected validation listener to be provided but was not", exception.message)
    }
}

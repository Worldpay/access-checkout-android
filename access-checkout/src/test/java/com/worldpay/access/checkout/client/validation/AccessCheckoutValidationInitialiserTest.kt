package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutValidationInitialiserTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val expiryDate = EditText(context)
    private val cvc = EditText(context)
    private val pan = EditText(context)

    private val baseUrl = "http://localhost"
    private val cardValidationListener: AccessCheckoutCardValidationListener = mock()
    private val cvcValidationListener: AccessCheckoutCvcValidationListener = mock()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    @Before
    fun setUp() {
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
    }

    @Test
    fun `should be able to initialise the validation for card validation`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvc(cvc)
            .validationListener(cardValidationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertEquals(0, pan.filters.size)
        assertEquals(0, expiryDate.filters.size)
        assertEquals(0, cvc.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        assertEquals(1, pan.filters.size)
        assertEquals(1, expiryDate.filters.size)
        assertEquals(1, cvc.filters.size)
    }

    @Test
    fun `should be able to initialise the validation for cvc validation`() {
        val config = CvcValidationConfig.Builder()
            .cvc(cvc)
            .validationListener(cvcValidationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertEquals(0, cvc.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        assertEquals(1, cvc.filters.size)
    }

}

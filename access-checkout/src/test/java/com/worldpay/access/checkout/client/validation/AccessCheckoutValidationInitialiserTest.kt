package com.worldpay.access.checkout.client.validation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.worldpay.access.checkout.client.testutil.AbstractValidationIntegrationTest
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvcValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvcValidationListener
import com.worldpay.access.checkout.validation.filters.CvcLengthFilter
import com.worldpay.access.checkout.validation.filters.ExpiryDateLengthFilter
import com.worldpay.access.checkout.validation.filters.PanNumericFilter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutValidationInitialiserTest : AbstractValidationIntegrationTest() {

    private val acceptedCardBrands = arrayOf("VISA")

    private val baseUrl = "https://localhost:8443"
    private val cvcValidationListener: AccessCheckoutCvcValidationListener = mock()
    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()
    private val checkoutId = "checkout id"

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
            .acceptedCardBrands(acceptedCardBrands)
            .validationListener(cardValidationListener)
            .lifecycleOwner(lifecycleOwner)
            .checkoutId(checkoutId)
            .build()

        assertEquals(0, pan.filters.size)
        assertEquals(0, expiryDate.filters.size)
        assertEquals(0, cvc.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        // Versions of Android >= 30 add an additional LengthFilter to limit the input to 5,000 chars
        // So to be flexible with our testing we just test that the 1st filter is the one
        // expected to be added by our SDK
        assertTrue(pan.filters[0] is PanNumericFilter)
        assertTrue(expiryDate.filters[0] is ExpiryDateLengthFilter)
        assertTrue(cvc.filters[0] is CvcLengthFilter)
    }

    @Test
    fun `should be able to initialise the validation for cvc validation`() {
        val config = CvcValidationConfig.Builder()
            .cvc(cvc)
            .validationListener(cardValidationListener)
            .validationListener(cvcValidationListener)
            .lifecycleOwner(lifecycleOwner)
            .build()

        assertEquals(0, cvc.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        assertTrue(cvc.filters[0] is CvcLengthFilter)
    }
}

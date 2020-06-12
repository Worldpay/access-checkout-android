package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.client.validation.config.CvvValidationConfig
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCvvValidationListener
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AccessCheckoutValidationInitialiserTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val expiryDate = EditText(context)
    private val cvv = EditText(context)
    private val pan = EditText(context)

    private val baseUrl = "http://localhost"
    private val cardValidationListener: AccessCheckoutCardValidationListener = mock()
    private val cvvValidationListener: AccessCheckoutCvvValidationListener = mock()

    @Test
    fun `should be able to initialise the validation for card validation`() {
        val config = CardValidationConfig.Builder()
            .baseUrl(baseUrl)
            .pan(pan)
            .expiryDate(expiryDate)
            .cvv(cvv)
            .validationListener(cardValidationListener)
            .build()

        assertEquals(0, pan.filters.size)
        assertEquals(0, expiryDate.filters.size)
        assertEquals(0, cvv.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        assertEquals(1, pan.filters.size)
        assertEquals(1, expiryDate.filters.size)
        assertEquals(1, cvv.filters.size)
    }

    @Test
    fun `should be able to initialise the validation for cvv validation`() {
        val config = CvvValidationConfig.Builder()
            .cvv(cvv)
            .validationListener(cvvValidationListener)
            .build()

        assertEquals(0, cvv.filters.size)

        AccessCheckoutValidationInitialiser.initialise(config)

        assertEquals(1, cvv.filters.size)
    }

}

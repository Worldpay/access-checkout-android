package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.AccessCheckoutCardValidationListener
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>()
    private val cardConfiguration = mock<CardConfiguration>()

    private val cvvEditText = mock<EditText>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        textWatcherFactory = TextWatcherFactory(accessCheckoutValidationListener)
    }

    @Test
    fun `should get pan text watcher`() {
        assertTrue(textWatcherFactory.createPanTextWatcher(panEditText, cvvEditText, cardConfiguration) is PANTextWatcher)
    }

    @Test
    fun `should get expiry month text watcher`() {
        assertTrue(textWatcherFactory.createExpiryMonthTextWatcher(cardConfiguration) is ExpiryMonthTextWatcher)
    }

    @Test
    fun `should get expiry year text watcher`() {
        assertTrue(textWatcherFactory.createExpiryYearTextWatcher(cardConfiguration) is ExpiryYearTextWatcher)
    }

    @Test
    fun `should get cvv text watcher`() {
        assertTrue(textWatcherFactory.createCvvTextWatcher(cvvEditText, cardConfiguration) is CVVTextWatcher)
    }

}

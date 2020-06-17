package com.worldpay.access.checkout.validation.listeners.text

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.validation.listener.AccessCheckoutCardValidationListener
import com.worldpay.access.checkout.validation.state.CardValidationStateManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val accessCheckoutValidationListener = mock<AccessCheckoutCardValidationListener>()
    private val cardConfiguration = mock<CardConfiguration>()

    private val cvvEditText = mock<EditText>()
    private val expiryDateEditText = mock<EditText>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        textWatcherFactory = TextWatcherFactory(accessCheckoutValidationListener, mock<CardValidationStateManager>())
    }

    @Test
    fun `should get pan text watcher`() {
        assertTrue(textWatcherFactory.createPanTextWatcher(cvvEditText, cardConfiguration) is PANTextWatcher)
    }

    @Test
    fun `should get expiry date text watcher`() {
        assertTrue(textWatcherFactory.createExpiryDateTextWatcher(expiryDateEditText) is ExpiryDateTextWatcher)
    }

    @Test
    fun `should get cvv text watcher`() {
        assertTrue(textWatcherFactory.createCvvTextWatcher() is CVVTextWatcher)
    }

}

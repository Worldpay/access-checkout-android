package com.worldpay.access.checkout.validation.watchers

import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.client.AccessCheckoutValidationListener
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val validationListener = mock<AccessCheckoutValidationListener>()
    private val cardDetailComponents = mock<CardDetailComponents>()
    private val cardConfiguration = mock<CardConfiguration>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        textWatcherFactory = TextWatcherFactory(
            validationListener = validationListener,
            cardDetailComponents = cardDetailComponents,
            cardConfiguration = cardConfiguration
        )
    }

    @Test
    fun `should get pan text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(PAN) is PANTextWatcher)
    }

    @Test
    fun `should get expiry month text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(EXPIRY_MONTH) is ExpiryMonthTextWatcher)
    }

    @Test
    fun `should get expiry year text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(EXPIRY_YEAR) is ExpiryYearTextWatcher)
    }

    @Test
    fun `should get cvv text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(CVV) is CVVTextWatcher)
    }

}
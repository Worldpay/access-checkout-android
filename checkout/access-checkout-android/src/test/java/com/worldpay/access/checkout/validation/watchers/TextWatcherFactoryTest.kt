package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.validation.ValidationResultHandler
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TextWatcherFactoryTest {

    private val validationResultHandler = mock<ValidationResultHandler>()
    private val cardConfiguration = mock<CardConfiguration>()

    private val cvvEditText = mock<EditText>()
    private val panEditText = mock<EditText>()
    private val expiryMonthEditText = mock<EditText>()
    private val expiryYearEditText = mock<EditText>()

    private lateinit var textWatcherFactory: TextWatcherFactory

    @Before
    fun setup() {
        val cardDetailComponents = CardDetailComponents(
            pan = panEditText,
            expiryMonth = expiryMonthEditText,
            expiryYear = expiryYearEditText,
            cvv = cvvEditText
        )

        textWatcherFactory = TextWatcherFactory(
            validationResultHandler = validationResultHandler,
            cardDetailComponents = cardDetailComponents
        )
    }

    @Test
    fun `should get pan text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(PAN, cardConfiguration) is PANTextWatcher)
    }

    @Test
    fun `should get expiry month text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(EXPIRY_MONTH, cardConfiguration) is ExpiryMonthTextWatcher)
    }

    @Test
    fun `should get expiry year text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(EXPIRY_YEAR, cardConfiguration) is ExpiryYearTextWatcher)
    }

    @Test
    fun `should get cvv text watcher`() {
        assertTrue(textWatcherFactory.createTextWatcher(CVV, cardConfiguration) is CVVTextWatcher)
    }

}
package com.worldpay.access.checkout.validation.controller

import android.text.TextWatcher
import android.widget.EditText
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

internal class CardDetailsValidationController(
    private val panEditText: EditText,
    private val expiryMonthEditText: EditText,
    private val expiryYearEditText: EditText,
    private val cvvEditText: EditText,
    baseUrl: String,
    cardConfigurationClient: CardConfigurationClient,
    private var textWatcherFactory: TextWatcherFactory
) {

    private lateinit var panTextWatcher: TextWatcher
    private lateinit var expiryMonthTextWatcher: TextWatcher
    private lateinit var expiryYearTextWatcher: TextWatcher
    private lateinit var cvvTextWatcher: TextWatcher

    init {
        addTextChangedListeners(CardConfiguration(emptyList(), CARD_DEFAULTS))

        // fetch remote cardConfiguration - resets the cardConfiguration field if a remote one is found
        cardConfigurationClient.getCardConfiguration(baseUrl, getCardConfigurationCallback())
    }

    private fun addTextChangedListeners(cardConfiguration: CardConfiguration) {
        panTextWatcher = textWatcherFactory.createPanTextWatcher(panEditText, cardConfiguration)
        expiryMonthTextWatcher = textWatcherFactory.createExpiryMonthTextWatcher(expiryMonthEditText, cardConfiguration)
        expiryYearTextWatcher = textWatcherFactory.createExpiryYearTextWatcher(expiryYearEditText, cardConfiguration)
        cvvTextWatcher = textWatcherFactory.createCvvTextWatcher(cvvEditText, panEditText, cardConfiguration)

        panEditText.addTextChangedListener(panTextWatcher)
        expiryMonthEditText.addTextChangedListener(expiryMonthTextWatcher)
        expiryYearEditText.addTextChangedListener(expiryYearTextWatcher)
        cvvEditText.addTextChangedListener(cvvTextWatcher)
    }

    private fun removeTextChangedListeners() {
        panEditText.removeTextChangedListener(panTextWatcher)
        expiryMonthEditText.removeTextChangedListener(expiryMonthTextWatcher)
        expiryYearEditText.removeTextChangedListener(expiryYearTextWatcher)
        cvvEditText.removeTextChangedListener(cvvTextWatcher)
    }

    private fun getCardConfigurationCallback(): Callback<CardConfiguration> {
        return object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let {cardConfig ->
                    debugLog(javaClass.simpleName, "Retrieved remote card configuration")
                    removeTextChangedListeners()
                    addTextChangedListeners(cardConfig)
                }
                error?.let {
                    debugLog(javaClass.simpleName,"Error while fetching card configuration: $it")
                }
            }
        }
    }

}

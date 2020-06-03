package com.worldpay.access.checkout.validation

import android.text.TextWatcher
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import com.worldpay.access.checkout.validation.card.CardDetailComponents
import com.worldpay.access.checkout.validation.card.CardDetailType.*
import com.worldpay.access.checkout.validation.card.CardDetailType.CVV
import com.worldpay.access.checkout.validation.watchers.TextWatcherFactory

internal class AccessCheckoutValidationController(
    baseUrl: String,
    private val cardDetailComponents: CardDetailComponents,
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
        panTextWatcher = textWatcherFactory.createTextWatcher(PAN, cardConfiguration)
        expiryMonthTextWatcher = textWatcherFactory.createTextWatcher(EXPIRY_MONTH, cardConfiguration)
        expiryYearTextWatcher = textWatcherFactory.createTextWatcher(EXPIRY_YEAR, cardConfiguration)
        cvvTextWatcher = textWatcherFactory.createTextWatcher(CVV, cardConfiguration)

        cardDetailComponents.pan.addTextChangedListener(panTextWatcher)
        cardDetailComponents.expiryMonth.addTextChangedListener(expiryMonthTextWatcher)
        cardDetailComponents.expiryYear.addTextChangedListener(expiryYearTextWatcher)
        cardDetailComponents.cvv.addTextChangedListener(cvvTextWatcher)
    }

    private fun removeTextChangedListeners() {
        cardDetailComponents.pan.removeTextChangedListener(panTextWatcher)
        cardDetailComponents.expiryMonth.removeTextChangedListener(expiryMonthTextWatcher)
        cardDetailComponents.expiryYear.removeTextChangedListener(expiryYearTextWatcher)
        cardDetailComponents.cvv.removeTextChangedListener(cvvTextWatcher)
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

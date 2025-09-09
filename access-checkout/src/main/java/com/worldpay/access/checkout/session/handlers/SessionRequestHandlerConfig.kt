package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull

/**
 * This class holds the configuration to be passed to [SessionRequestHandlerFactory] for constructing a [SessionRequestHandler]
 *
 * @property checkoutId [String] that represents the checkout id of the merchant given to the client at time of registration
 * @property context [Context] that represents the application
 */
internal class SessionRequestHandlerConfig private constructor(
    private val checkoutId: String,
    private val context: Context
) {
    fun getCheckoutId() = checkoutId
    fun getContext() = context

    /**
     * A builder for [SessionRequestHandlerConfig]
     */
    data class Builder(
        private var checkoutId: String? = null,
        private var context: Context? = null,
    ) {

        fun checkoutId(checkoutId: String) = apply { this.checkoutId = checkoutId }

        fun context(context: Context) = apply { this.context = context }

        fun build(): SessionRequestHandlerConfig {
            validateNotNull(checkoutId, "merchant id")
            validateNotNull(context, "context")

            return SessionRequestHandlerConfig(
                checkoutId = checkoutId!!,
                context = context!!,
            )
        }
    }
}

package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull
import java.net.URL

/**
 * This class holds the configuration to be passed to [SessionRequestHandlerFactory] for constructing a [SessionRequestHandler]
 *
 * @property checkoutId [String] that represents the checkout id of the merchant given to the client at time of registration
 * @property context [Context] that represents the application
 * @property externalSessionResponseListener - An external [SessionResponseListener] that is notified on http requests
 */
internal class SessionRequestHandlerConfig private constructor(
    private val checkoutId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener
) {
    fun getCheckoutId() = checkoutId
    fun getContext() = context
    fun getExternalSessionResponseListener() = externalSessionResponseListener

    /**
     * A builder for [SessionRequestHandlerConfig]
     */
    data class Builder(
        private var checkoutId: String? = null,
        private var context: Context? = null,
        private var externalSessionResponseListener: SessionResponseListener? = null
    ) {

        fun checkoutId(checkoutId: String) = apply { this.checkoutId = checkoutId }

        fun context(context: Context) = apply { this.context = context }

        fun externalSessionResponseListener(
            externalSessionResponseListener: SessionResponseListener
        ) =
            apply { this.externalSessionResponseListener = externalSessionResponseListener }

        fun build(): SessionRequestHandlerConfig {
            validateNotNull(checkoutId, "merchant id")
            validateNotNull(context, "context")
            validateNotNull(externalSessionResponseListener, "session response listener")

            return SessionRequestHandlerConfig(
                checkoutId = checkoutId!!,
                context = context!!,
                externalSessionResponseListener = externalSessionResponseListener!!
            )
        }
    }
}

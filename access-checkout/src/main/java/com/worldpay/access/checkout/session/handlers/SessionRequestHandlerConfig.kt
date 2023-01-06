package com.worldpay.access.checkout.session.handlers

import android.content.Context
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.util.PropertyValidationUtil.validateNotNull
import java.net.URL

/**
 * This class holds the configuration to be passed to [SessionRequestHandlerFactory] for constructing a [SessionRequestHandler]
 *
 * @property baseUrl [URL] that represents the base url
 * @property merchantId [String] that represents the id of the merchant given to the client at time of registration
 * @property context [Context] that represents the application
 * @property externalSessionResponseListener - An external [SessionResponseListener] that is notified on http requests
 */
internal class SessionRequestHandlerConfig private constructor(
    private val baseUrl: URL,
    private val merchantId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener
) {

    fun getBaseUrl() = baseUrl
    fun getMerchantId() = merchantId
    fun getContext() = context
    fun getExternalSessionResponseListener() = externalSessionResponseListener

    /**
     * A builder for [SessionRequestHandlerConfig]
     */
    data class Builder(
        private var baseUrl: URL? = null,
        private var merchantId: String? = null,
        private var context: Context? = null,
        private var externalSessionResponseListener: SessionResponseListener? = null
    ) {

        fun baseUrl(baseUrl: URL) = apply { this.baseUrl = baseUrl }

        fun merchantId(merchantId: String) = apply { this.merchantId = merchantId }

        fun context(context: Context) = apply { this.context = context }

        fun externalSessionResponseListener(
            externalSessionResponseListener: SessionResponseListener
        ) =
            apply { this.externalSessionResponseListener = externalSessionResponseListener }

        fun build(): SessionRequestHandlerConfig {
            validateNotNull(baseUrl, "base url")
            validateNotNull(merchantId, "merchant id")
            validateNotNull(context, "context")
            validateNotNull(externalSessionResponseListener, "session response listener")

            return SessionRequestHandlerConfig(
                baseUrl = baseUrl!!,
                merchantId = merchantId!!,
                context = context!!,
                externalSessionResponseListener = externalSessionResponseListener!!
            )
        }
    }
}

package com.worldpay.access.checkout.session.request.handlers

import android.content.Context
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull
import com.worldpay.access.checkout.views.SessionResponseListener

internal class SessionRequestHandlerConfig private constructor(
    private val baseUrl: String,
    private val merchantId: String,
    private val context: Context,
    private val externalSessionResponseListener: SessionResponseListener
) {

    fun getBaseUrl() = baseUrl
    fun getMerchantId() = merchantId
    fun getContext() = context
    fun getExternalSessionResponseListener() = externalSessionResponseListener

    data class Builder(
        private var baseUrl: String? = null,
        private var merchantId: String? = null,
        private var context: Context? = null,
        private var externalSessionResponseListener: SessionResponseListener? = null
    ) {

        fun baseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }

        fun merchantId(merchantId: String) = apply { this.merchantId = merchantId }

        fun context(context: Context) = apply { this.context = context }

        fun externalSessionResponseListener(externalSessionResponseListener: SessionResponseListener) =
            apply { this.externalSessionResponseListener = externalSessionResponseListener }

        fun build(): SessionRequestHandlerConfig {
            validateNotNull(baseUrl, "base url")
            validateNotNull(merchantId, "merchant ID")
            validateNotNull(context, "context")
            validateNotNull(externalSessionResponseListener, "session response listener")

            return SessionRequestHandlerConfig(
                baseUrl = baseUrl as String,
                merchantId = merchantId as String,
                context = context as Context,
                externalSessionResponseListener = externalSessionResponseListener as SessionResponseListener
            )
        }

    }

}
package com.worldpay.access.checkout.api

import android.os.AsyncTask
import android.util.Log
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutClientError
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutHttpException
import java.net.URL


internal class RequestDispatcher constructor(
    private val path: String,
    private val callback: Callback<SessionResponse>,
    private val sessionClient: SessionClient
) : AsyncTask<CardSessionRequest, Any, SessionResponse>() {

    private var exception: Exception? = null

    override fun doInBackground(vararg params: CardSessionRequest): SessionResponse? {
        return try {
            val sessionRequest = getSessionRequest(params)
            sessionClient.getSessionResponse(URL(path), sessionRequest)
        } catch (ex: AccessCheckoutClientError) {
            exception = ex
            return null
        } catch (ex: AccessCheckoutHttpException) {
            exception = if (!ex.message.isNullOrBlank()) {
                ex
            } else {
                AccessCheckoutHttpException("An exception was thrown when trying to establish a connection", ex)
            }

            return null
        } catch (ex: Exception) {
            Log.e("RequestDispatcher", "Received exception: $ex")
            exception = ex
            return null
        }
    }

    private fun getSessionRequest(params: Array<out CardSessionRequest>): CardSessionRequest {
        if (params.isEmpty()) {
            throw AccessCheckoutHttpException("No request was supplied for sending", null)
        }
        return params[0]
    }

    override fun onPostExecute(result: SessionResponse?) {
        result?.let {
            callback.onResponse(null, it)
        }

        exception?.let {
            callback.onResponse(it, null)
        }
    }

    companion object {
        @JvmStatic
        fun buildDispatcher(path: String, callback: Callback<SessionResponse>): RequestDispatcher {
            val sessionClientFactory = SessionClientFactory()
            val client = sessionClientFactory.createClient()
            return RequestDispatcher(path, callback, client)
        }
    }
}
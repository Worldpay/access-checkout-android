package com.worldpay.access.checkout.session.api.request

import android.os.AsyncTask
import android.util.Log
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutClientError
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutHttpException
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.session.api.client.SessionClient
import com.worldpay.access.checkout.session.api.response.SessionResponseInfo
import java.net.URL

/**
 * This class is responsible for dispatching an asynchronous request for a session and
 * returning the response via a callback.
 *
 * @see AsyncTask
 *
 * @property[path] the URL for the API
 * @property[callback] callback via which the [SessionResponseInfo] is returned
 * @property[sessionClient] an implementation of [SessionClient] to handle the request
 */
internal class RequestDispatcher constructor(
    private val path: String,
    private val callback: Callback<SessionResponseInfo>,
    private val sessionClient: SessionClient
) : AsyncTask<SessionRequestInfo, Any, SessionResponseInfo>() {

    private var exception: Exception? = null

    override fun doInBackground(vararg params: SessionRequestInfo): SessionResponseInfo? {
        return try {
            val sessionRequestInfo = getSessionRequestInfo(params)
            val responseBody = sessionClient.getSessionResponse(URL(path), sessionRequestInfo.requestBody)

            SessionResponseInfo.Builder()
                .responseBody(responseBody)
                .sessionType(sessionRequestInfo.sessionType)
                .build()

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

    private fun getSessionRequestInfo(params: Array<out SessionRequestInfo>): SessionRequestInfo {
        if (params.isEmpty()) {
            throw AccessCheckoutHttpException("No request was supplied for sending", null)
        }
        return params[0]
    }

    override fun onPostExecute(result: SessionResponseInfo?) {
        result?.let {
            callback.onResponse(null, it)
        }

        exception?.let {
            callback.onResponse(it, null)
        }
    }

}
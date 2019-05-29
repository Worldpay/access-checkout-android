package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.serialization.ClientErrorDeserializer
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

internal class HttpClient(private val urlFactory: URLFactory = URLFactoryImpl(),
                          private val clientErrorDeserializer: Deserializer<AccessCheckoutClientError> = ClientErrorDeserializer()) {

    @Throws(AccessCheckoutException::class)
    fun <Request : Serializable, Response> doPost(
        url: URL, request: Request, headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        var httpUrlConn: HttpURLConnection? = null

        try {
            val requestBody = serializer.serialize(request)

            httpUrlConn = url.openConnection() as HttpURLConnection
            httpUrlConn.requestMethod = POST_METHOD
            setRequestProperties(httpUrlConn, headers)
            httpUrlConn.doOutput = true
            httpUrlConn.connectTimeout = 15000
            httpUrlConn.readTimeout = 10000
            httpUrlConn.setChunkedStreamingMode(0)


            val outputStream: OutputStream = BufferedOutputStream(httpUrlConn.outputStream)
            OutputStreamWriter(outputStream).use {
                it.write(requestBody)
                it.flush()
                return if (isRedirectStatus(httpUrlConn)) {
                    handleRedirect(httpUrlConn, request, headers, serializer, deserializer)
                } else {
                    val responseData = getBodyStream(httpUrlConn)
                    deserializer.deserialize(responseData)
                }
            }
        } catch (ex: AccessCheckoutClientError) {
            throw ex
        } catch (ex: AccessCheckoutException) {
            val message = ex.message
            throw AccessCheckoutHttpException(message, ex)
        } catch (ex: Exception) {
            val message = ex.message ?: "An exception was thrown when trying to establish a connection"
            throw AccessCheckoutHttpException(message, ex)
        } finally {
            httpUrlConn?.disconnect()
        }
    }

    @Throws(AccessCheckoutException::class)
    fun <Response> doGet(url: URL, deserializer: Deserializer<Response>): Response {
        var httpUrlConn: HttpURLConnection? = null
        try {
            httpUrlConn = url.openConnection() as HttpURLConnection
            httpUrlConn.requestMethod = GET_METHOD
            httpUrlConn.connectTimeout = 15000
            httpUrlConn.readTimeout = 10000

            return if (isRedirectStatus(httpUrlConn)) {
                handleRedirect(httpUrlConn, deserializer)
            } else {
                val responseBody = getBodyStream(httpUrlConn)
                deserializer.deserialize(responseBody)
            }

        } catch (ex: AccessCheckoutException) {
            throw AccessCheckoutHttpException(ex.message, ex)
        } catch (ex: Exception) {
            val message = ex.message ?: "An exception was thrown when trying to establish a connection"
            throw AccessCheckoutHttpException(message, ex)
        } finally {
            httpUrlConn?.disconnect()
        }
    }

    private fun setRequestProperties(httpURLConnection: HttpURLConnection, headers: Map<String, String>) {
        headers.forEach { httpURLConnection.setRequestProperty(it.key, it.value) }
    }

    private fun getBodyStream(conn: HttpURLConnection): String {
        val successfulHttpRange = 200..299

        if (conn.responseCode !in successfulHttpRange) {
            throw getClientError(conn)
        }

        return getResponseFromInputStream(conn.inputStream)
    }

    private fun getClientError(conn: HttpURLConnection): AccessCheckoutException {
        var clientException: AccessCheckoutClientError? = null
        var errorData: String? = null

        if (conn.errorStream != null) {
            errorData = getResponseFromInputStream(conn.errorStream)
            clientException = clientErrorDeserializer.deserialize(errorData)
        }

        return clientException.let { it } ?: AccessCheckoutHttpException(getMessage(conn, errorData), null)
    }

    private fun getMessage(conn: HttpURLConnection, errorData: String?): String {
        var message = "Error message was: ${conn.responseMessage}"

        if (errorData != null && errorData.isNotEmpty()) {
            message = "$message. Error response was: $errorData"
        }
        return message
    }

    private fun getResponseFromInputStream(inputStream: InputStream): String {
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val response = StringBuilder()
            var currentLine: String? = null

            while ({ currentLine = reader.readLine(); currentLine }() != null)
                response.append(currentLine)

            response.toString()
        }
    }

    private fun <Request : Serializable, Response> handleRedirect(
        httpUrlConn: HttpURLConnection,
        request: Request,
        headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        fun postFunc(url: String) = doPost(urlFactory.getURL(url), request, headers, serializer, deserializer)
        return handleRedirect(httpUrlConn, ::postFunc)
    }

    private fun <Response> handleRedirect(httpUrlConn: HttpURLConnection, deserializer: Deserializer<Response>): Response {
        fun getFunc(url: String) = doGet(urlFactory.getURL(url), deserializer)
        return handleRedirect(httpUrlConn, ::getFunc)
    }

    private fun <Response> handleRedirect(httpUrlConn: HttpURLConnection, exec: (String) -> Response): Response {
        val location = httpUrlConn.getHeaderField(LOCATION)
        if (location.isNullOrBlank()) {
            throw AccessCheckoutHttpException("Response from server was a redirect HTTP response code: ${httpUrlConn.responseCode} but did not include a Location header", null)
        }
        return exec(location)
    }

    private fun isRedirectStatus(httpUrlConn: HttpURLConnection) = httpUrlConn.responseCode in 300..399


    companion object {
        private const val POST_METHOD = "POST"
        private const val GET_METHOD = "GET"
        private const val LOCATION = "Location"
    }
}

internal interface URLFactory {
    fun getURL(url: String): URL
}

internal class URLFactoryImpl: URLFactory {
    override fun getURL(url: String): URL = URL(url)
}

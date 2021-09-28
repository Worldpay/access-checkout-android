package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.serialization.ClientErrorDeserializer
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Serializable
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class HttpsClient(
    private val urlFactory: URLFactory = URLFactoryImpl(),
    private val clientErrorDeserializer: Deserializer<AccessCheckoutException> = ClientErrorDeserializer()
) {

    @Throws(AccessCheckoutException::class)
    fun <Request : Serializable, Response> doPost(
        url: URL,
        request: Request,
        headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        var httpsUrlConn: HttpsURLConnection? = null

        try {
            val requestBody = serializer.serialize(request)

            httpsUrlConn = url.openConnection() as HttpsURLConnection
            httpsUrlConn.requestMethod = POST_METHOD
            setRequestProperties(httpsUrlConn, headers)
            httpsUrlConn.doOutput = true
            httpsUrlConn.connectTimeout = CONNECT_TIMEOUT
            httpsUrlConn.readTimeout = READ_TIMEOUT
            httpsUrlConn.setChunkedStreamingMode(0)

            val outputStream: OutputStream = BufferedOutputStream(httpsUrlConn.outputStream)
            OutputStreamWriter(outputStream).use {
                it.write(requestBody)
                it.flush()

                val responseData = when (httpsUrlConn.responseCode) {
                    in successfulHttpRange -> getResponseData(httpsUrlConn.inputStream)
                    in redirectHttpRange -> return handleRedirect(httpsUrlConn, request, headers, serializer, deserializer)
                    in clientErrorHttpRange -> throw getClientError(httpsUrlConn)
                    else -> throw getServerError(httpsUrlConn)
                }

                return deserializer.deserialize(responseData)
            }
        } catch (ex: AccessCheckoutException) {
            throw ex
        } catch (ex: Exception) {
            val message = ex.message ?: "An exception was thrown when trying to establish a connection"
            throw AccessCheckoutException(message = message, cause = ex)
        } finally {
            httpsUrlConn?.disconnect()
        }
    }

    @Throws(AccessCheckoutException::class)
    fun <Response> doGet(
        url: URL,
        deserializer: Deserializer<Response>,
        headers: Map<String, String> = mapOf()
    ): Response {
        var httpsUrlConn: HttpsURLConnection? = null
        try {

            httpsUrlConn = url.openConnection() as HttpsURLConnection
            httpsUrlConn.requestMethod = GET_METHOD
            setRequestProperties(httpsUrlConn, headers)
            httpsUrlConn.connectTimeout = CONNECT_TIMEOUT
            httpsUrlConn.readTimeout = READ_TIMEOUT

            val responseData = when (httpsUrlConn.responseCode) {
                in successfulHttpRange -> getResponseData(httpsUrlConn.inputStream)
                in redirectHttpRange -> return handleRedirect(httpsUrlConn, deserializer)
                in clientErrorHttpRange -> throw getClientError(httpsUrlConn)
                else -> throw getServerError(httpsUrlConn)
            }

            return deserializer.deserialize(responseData)
        } catch (ex: AccessCheckoutException) {
            throw ex
        } catch (ex: Exception) {
            val message = ex.message ?: "An exception was thrown when trying to establish a connection"
            throw AccessCheckoutException(message, ex)
        } finally {
            httpsUrlConn?.disconnect()
        }
    }

    private fun setRequestProperties(
        HttpsURLConnection: HttpsURLConnection,
        headers: Map<String, String>
    ) {
        headers.forEach { HttpsURLConnection.setRequestProperty(it.key, it.value) }
    }

    private fun getClientError(conn: HttpsURLConnection): AccessCheckoutException {
        var clientException: AccessCheckoutException? = null
        var errorData: String? = null

        if (conn.errorStream != null) {
            errorData = getResponseData(conn.errorStream)
            clientException = clientErrorDeserializer.deserialize(errorData)
        }

        return clientException ?: AccessCheckoutException(getMessage(conn, errorData))
    }

    private fun getServerError(conn: HttpsURLConnection): AccessCheckoutException {
        if (conn.errorStream != null) {
            val errorData = getResponseData(conn.errorStream)
            val message = getMessage(conn, errorData)
            return AccessCheckoutException(message)
        }
        return AccessCheckoutException("A server error occurred when trying to make the request")
    }

    private fun getMessage(conn: HttpsURLConnection, errorData: String?): String {
        var message = "Error message was: ${conn.responseMessage}"

        if (!errorData.isNullOrEmpty()) {
            message = "$message. Error response was: $errorData"
        }
        return message
    }

    private fun getResponseData(inputStream: InputStream): String {
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val response = StringBuilder()
            var currentLine: String?

            while (run {
                currentLine = reader.readLine()
                currentLine
            } != null
            )
                response.append(currentLine)

            response.toString()
        }
    }

    private fun <Request : Serializable, Response> handleRedirect(
        httpsUrlConn: HttpsURLConnection,
        request: Request,
        headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        fun postFunc(url: String) = doPost(urlFactory.getURL(url), request, headers, serializer, deserializer)
        return handleRedirect(httpsUrlConn, ::postFunc)
    }

    private fun <Response> handleRedirect(
        httpsUrlConn: HttpsURLConnection,
        deserializer: Deserializer<Response>
    ): Response {
        fun getFunc(url: String) = doGet(urlFactory.getURL(url), deserializer)
        return handleRedirect(httpsUrlConn, ::getFunc)
    }

    private fun <Response> handleRedirect(
        httpsUrlConn: HttpsURLConnection,
        exec: (String) -> Response
    ): Response {
        val location = httpsUrlConn.getHeaderField(LOCATION)
        if (location.isNullOrBlank()) {
            throw AccessCheckoutException("Response from server was a redirect HTTP response code: ${httpsUrlConn.responseCode} but did not include a Location header")
        }
        return exec(location)
    }

    internal companion object {
        private const val POST_METHOD = "POST"
        private const val GET_METHOD = "GET"
        private const val LOCATION = "Location"

        private const val CONNECT_TIMEOUT = 30000
        private const val READ_TIMEOUT = 30000

        private val successfulHttpRange = 200..299
        private val redirectHttpRange = 300..399
        private val clientErrorHttpRange = 400..499
    }
}

internal interface URLFactory {
    fun getURL(url: String): URL
}

internal class URLFactoryImpl : URLFactory {
    override fun getURL(url: String): URL = URL(url)
}

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
            httpUrlConn.connectTimeout = CONNECT_TIMEOUT
            httpUrlConn.readTimeout = READ_TIMEOUT
            httpUrlConn.setChunkedStreamingMode(0)


            val outputStream: OutputStream = BufferedOutputStream(httpUrlConn.outputStream)
            OutputStreamWriter(outputStream).use {
                it.write(requestBody)
                it.flush()

                val responseData = when (httpUrlConn.responseCode) {
                    in successfulHttpRange -> getResponseData(httpUrlConn.inputStream)
                    in redirectHttpRange -> return handleRedirect(httpUrlConn, request, headers, serializer, deserializer)
                    in clientErrorHttpRange -> throw getClientError(httpUrlConn)
                    else -> throw getServerError(httpUrlConn)
                }

                return deserializer.deserialize(responseData)
            }
        } catch (ex: AccessCheckoutClientError) {
            throw ex
        } catch (ex: AccessCheckoutError) {
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
            httpUrlConn.connectTimeout = CONNECT_TIMEOUT
            httpUrlConn.readTimeout = READ_TIMEOUT

            val responseData = when (httpUrlConn.responseCode) {
                in successfulHttpRange -> getResponseData(httpUrlConn.inputStream)
                in redirectHttpRange -> return handleRedirect(httpUrlConn, deserializer)
                in clientErrorHttpRange -> throw getClientError(httpUrlConn)
                else -> throw getServerError(httpUrlConn)
            }

            return deserializer.deserialize(responseData)

        } catch (ex: AccessCheckoutError) {
            throw ex
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

    private fun getClientError(conn: HttpURLConnection): AccessCheckoutException {
        var clientException: AccessCheckoutClientError? = null
        var errorData: String? = null

        if (conn.errorStream != null) {
            errorData = getResponseData(conn.errorStream)
            clientException = clientErrorDeserializer.deserialize(errorData)
        }

        return clientException ?: AccessCheckoutHttpException(getMessage(conn, errorData))
    }

    private fun getServerError(conn: HttpURLConnection): AccessCheckoutException {
        if (conn.errorStream != null) {
            val errorData = getResponseData(conn.errorStream)
            val message = getMessage(conn, errorData)
            return AccessCheckoutError(message)
        }
        return AccessCheckoutError("A server error occurred when trying to make the request")
    }

    private fun getMessage(conn: HttpURLConnection, errorData: String?): String {
        var message = "Error message was: ${conn.responseMessage}"

        if (!errorData.isNullOrEmpty()) {
            message = "$message. Error response was: $errorData"
        }
        return message
    }

    private fun getResponseData(inputStream: InputStream): String {
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
            throw AccessCheckoutHttpException("Response from server was a redirect HTTP response code: ${httpUrlConn.responseCode} but did not include a Location header")
        }
        return exec(location)
    }

    companion object {
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

internal class URLFactoryImpl: URLFactory {
    override fun getURL(url: String): URL = URL(url)
}

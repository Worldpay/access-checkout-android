package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.NoWeakCipherSSLSocketFactory.Companion.noWeakCipherSSLSocketFactory
import com.worldpay.access.checkout.api.serialization.ClientErrorDeserializer
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serializable
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class HttpsClient(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val urlFactory: URLFactory = URLFactoryImpl(),
    private val clientErrorDeserializer: Deserializer<AccessCheckoutException> = ClientErrorDeserializer()
) {

    @Throws(AccessCheckoutException::class)
    suspend fun <Request : Serializable, Response> doPost(
        url: URL,
        request: Request,
        headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        return coroutineScope {
            async(dispatcher) {
                var httpsUrlConn: HttpsURLConnection? = null

                try {
                    val requestBody = serializer.serialize(request)

                    httpsUrlConn = createHttpsURLConnection(url)

                    httpsUrlConn.requestMethod = POST_METHOD
                    setRequestProperties(httpsUrlConn, headers)
                    httpsUrlConn.doOutput = true

                    // You will note that setChunkedStreamingMode() is not used although it would
                    // lead to better performances. This is because when used this property creates
                    // a memory leak where the request body remains in memory, this leaving the
                    // merchant application vulnerable to a heap inspection attack.

                    httpsUrlConn.outputStream.use { connectionOutputStream ->
                        BufferedOutputStream(connectionOutputStream).use { bufferedOutputStream ->
                            OutputStreamWriter(bufferedOutputStream).use { outputStreamWriter ->
                                outputStreamWriter.write(requestBody)
                                outputStreamWriter.flush()

                                val responseData = when (httpsUrlConn.responseCode) {

                                    in successfulHttpRange -> httpsUrlConn.inputStream.use { inputStream ->
                                        getResponseData(inputStream)
                                    }
                                    in redirectHttpRange -> return@async handleRedirect(
                                        httpsUrlConn,
                                        request,
                                        headers,
                                        serializer,
                                        deserializer
                                    )
                                    in clientErrorHttpRange -> throw getClientError(httpsUrlConn)
                                    else -> throw getServerError(httpsUrlConn)
                                }

                                return@async deserializer.deserialize(responseData)
                            }
                        }
                    }
                } catch (ex: AccessCheckoutException) {
                    throw ex
                } catch (ex: Exception) {
                    val message =
                        ex.message
                            ?: "An exception was thrown when trying to establish a connection"
                    throw AccessCheckoutException(message = message, cause = ex)
                } finally {
                    httpsUrlConn?.disconnect()
                }
            }
        }.await()
    }

    @Throws(AccessCheckoutException::class)
    suspend fun <Response> doGet(
        url: URL,
        deserializer: Deserializer<Response>,
        headers: Map<String, String> = mapOf()
    ): Response {
        return coroutineScope {
            async(dispatcher) {
                var httpsUrlConn: HttpsURLConnection? = null
                try {
                    httpsUrlConn = createHttpsURLConnection(url)

                    httpsUrlConn.requestMethod = GET_METHOD
                    setRequestProperties(httpsUrlConn, headers)

                    val responseData = when (httpsUrlConn.responseCode) {
                        in successfulHttpRange -> httpsUrlConn.inputStream.use { inputStream ->
                            getResponseData(inputStream)
                        }
                        in redirectHttpRange -> return@async handleRedirect(
                            httpsUrlConn,
                            deserializer
                        )
                        in clientErrorHttpRange -> throw getClientError(httpsUrlConn)
                        else -> throw getServerError(httpsUrlConn)
                    }

                    return@async deserializer.deserialize(responseData)
                } catch (ex: AccessCheckoutException) {
                    throw ex
                } catch (ex: Exception) {
                    val message =
                        ex.message
                            ?: "An exception was thrown when trying to establish a connection"
                    throw AccessCheckoutException(message, ex)
                } finally {
                    httpsUrlConn?.disconnect()
                }
            }
        }.await()
    }

    private fun createHttpsURLConnection(url: URL): HttpsURLConnection {
        val connection = url.openConnection()!! as HttpsURLConnection
        connection.connectTimeout = CONNECT_TIMEOUT
        connection.readTimeout = READ_TIMEOUT

        // Using a custom SSLSocketFactory that removes weak cipher suites from
        // enabled and supported cipher suites
        connection.sslSocketFactory = noWeakCipherSSLSocketFactory()

        return connection
    }

    private fun setRequestProperties(
        HttpsURLConnection: HttpsURLConnection,
        headers: Map<String, String>
    ) {
        headers.forEach { HttpsURLConnection.setRequestProperty(it.key, it.value) }

        // Connection header is "keep-alive" by default. We explicitly set it to "close" to instruct
        // the http library to close connections after receiving the response. This in return
        // ensures that the details of requests (headers, body) made to the backend do not
        // stay in memory
        HttpsURLConnection.setRequestProperty("Connection", "close")
    }

    private fun getClientError(conn: HttpsURLConnection): AccessCheckoutException {
        var clientException: AccessCheckoutException? = null
        var errorData: String? = null

        conn.errorStream?.use { errorStream ->
            errorData = getResponseData(errorStream)
            clientException = clientErrorDeserializer.deserialize(errorData!!)
        }

        return clientException ?: AccessCheckoutException(getMessage(conn, errorData))
    }

    private fun getServerError(conn: HttpsURLConnection): AccessCheckoutException {
        conn.errorStream?.use { errorStream ->
            val errorData = getResponseData(errorStream)
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
        return InputStreamReader(inputStream).use { inputStreamReader ->
            BufferedReader(inputStreamReader).use { reader ->
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
    }

    private suspend fun <Request : Serializable, Response> handleRedirect(
        httpsUrlConn: HttpsURLConnection,
        request: Request,
        headers: Map<String, String>,
        serializer: Serializer<Request>,
        deserializer: Deserializer<Response>
    ): Response {
        val location = httpsUrlConn.getHeaderField(LOCATION)
        if (location.isNullOrBlank()) {
            throw AccessCheckoutException("Response from server was a redirect HTTP response code: ${httpsUrlConn.responseCode} but did not include a Location header")
        }
        return doPost(urlFactory.getURL(location), request, headers, serializer, deserializer)
    }

    private suspend fun <Response> handleRedirect(
        httpsUrlConn: HttpsURLConnection,
        deserializer: Deserializer<Response>
    ): Response {
        val location = httpsUrlConn.getHeaderField(LOCATION)
        if (location.isNullOrBlank()) {
            throw AccessCheckoutException("Response from server was a redirect HTTP response code: ${httpsUrlConn.responseCode} but did not include a Location header")
        }
        return doGet(urlFactory.getURL(location), deserializer)
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

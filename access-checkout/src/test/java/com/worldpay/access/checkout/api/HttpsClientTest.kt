package com.worldpay.access.checkout.api

import com.google.common.collect.Maps.newHashMap
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.CoroutineTestRule
import com.worldpay.access.checkout.testutils.removeWhitespace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.anyInt
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.mock
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.Serializable
import java.net.ConnectException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runBlockingTest as runAsBlockingTest

@ExperimentalCoroutinesApi
class HttpsClientTest {

    private lateinit var urlFactory: URLFactory
    private lateinit var httpsClient: HttpsClient
    private lateinit var httpsUrlConnection: HttpsURLConnection
    private lateinit var deserializer: Deserializer<TestResponse>
    private lateinit var serializer: Serializer<TestRequest>
    private lateinit var clientErrorDeserializer: Deserializer<AccessCheckoutException>

    private val testRequest = TestRequest("abc")
    private val testRequestString = "{ \"property\": \"${testRequest.property}\" }"

    @Mock
    private lateinit var url: URL

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Before
    fun setup() = runAsBlockingTest {
        urlFactory = mock()
        deserializer = mock()
        serializer = mock()
        clientErrorDeserializer = mock()
        url = mock()
        httpsClient =
            HttpsClient(coroutinesTestRule.testDispatcher, urlFactory, clientErrorDeserializer)
        httpsUrlConnection = mock()
    }

    @Test
    fun givenValidRequest_doPost_shouldReturnSuccessfulResponse() = runAsBlockingTest {
        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse = TestResponse("abcdef")

        stubResponse(responseCode = 201, responseBody = testResponseAsString)

        assertTrue(httpsUrlConnection.requestProperties.isEmpty())

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doPost(
            url,
            testRequest,
            newHashMap(mapOf(Pair("key", "value"))),
            serializer,
            deserializer
        )

        assertEquals(testResponse, response)
        verify(httpsUrlConnection).setRequestProperty("key", "value")
    }

    @Test
    fun givenValidRequestAndHttp201AndDeserializationException_doPost_shouldThrowAccessCheckoutExceptionWithCauseAndMessage() =
        runAsBlockingTest {
            val responseBody = "{}"
            stubResponse(responseCode = 201, responseBody = responseBody)

            given(serializer.serialize(testRequest)).willReturn(testRequestString)
            given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "An exception was thrown when trying to establish a connection",
                    ace.message
                )
                assertTrue { ace.cause is java.lang.RuntimeException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenErrorWhenReadingResponseBody_doPost_shouldThrowAccessCheckoutException() =
        runAsBlockingTest {
            val errorMessage = "Some message"

            val inputStream = Mockito.mock(InputStream::class.java) {
                throw java.lang.RuntimeException(errorMessage)
            }

            stubResponse(responseCode = 201)
            given(httpsUrlConnection.inputStream).willReturn(inputStream)

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
                assertTrue(ace.cause is RuntimeException)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp500ErrorAndNoResponseBody_doPost_shouldThrowAccessCheckoutErrorWithDefaultMessage() =
        runAsBlockingTest {
            val errorMessage = "A server error occurred when trying to make the request"

            stubErrorResponse(responseCode = 500, message = "")

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp500ErrorAndBodyInResponse_doPost_shouldThrowAccessCheckoutErrorWithMessageFromServer() =
        runAsBlockingTest {
            stubErrorResponse(
                responseCode = 500,
                responseBody = "Some exception occurred",
                message = "Some http message"
            )

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Error message was: Some http message. Error response was: Some exception occurred",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp500ErrorWithResponseBody_doPost_shouldThrowAccessCheckoutExceptionWithErrorResponseFromServer() =
        runAsBlockingTest {
            val errorMessage = "some error message"
            stubErrorResponse(responseCode = 500, responseBody = "", message = errorMessage)
            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals("Error message was: $errorMessage", ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp500ErrorAndExceptionIsThrownWhenReadingErrorStream_doPost_shouldThrowAccessCheckoutExceptionWithThatErrorResponseFromServer() =
        runAsBlockingTest {
            val expectedException = RuntimeException("some unhandled exception")
            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            given(url.openConnection()).willReturn(httpsUrlConnection)
            given(httpsUrlConnection.responseCode).willReturn(500)
            given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())

            val mockErrorStream = mock<InputStream>()
            given(mockErrorStream.read(any(), anyInt(), anyInt())).willThrow(expectedException)
            given(httpsUrlConnection.errorStream).willReturn(mockErrorStream)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ex: AccessCheckoutException) {
                assertEquals(expectedException.message, ex.message)
                assertEquals(expectedException, ex.cause)
            } catch (ex: RuntimeException) {
                fail("Expected RuntimeException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttpStatusCodeBelow200_doPost_shouldThrowException() = runAsBlockingTest {
        val errorMessage = "A server error occurred when trying to make the request"

        stubErrorResponse(responseCode = 100, message = "")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        try {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals(errorMessage, ace.message)
        } catch (ex: Exception) {
            fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
        }
    }

    @Test
    fun givenHttp400ErrorWithResponseBody_doPost_shouldThrowAccessCheckoutExceptionWithErrorResponseFromServer() =
        runAsBlockingTest {
            val errorMessage =
                "bodyDoesNotMatchSchema : The json body provided does not match the expected schema"
            val responseBody =
                removeWhitespace(
                    """{
                            "errorName": "bodyDoesNotMatchSchema",
                            "message": "The json body provided does not match the expected schema",
                            "validationErrors": [
                                {
                                    "errorName": "fieldHasInvalidValue",
                                    "message": "Identity is invalid",
                                    "jsonPath": "${'$'}.identity"
                                }
                            ]
                        }"""
                )

            stubErrorResponse(
                responseCode = 400,
                responseBody = responseBody,
                message = errorMessage
            )
            given(clientErrorDeserializer.deserialize(responseBody)).willThrow(
                AccessCheckoutException(errorMessage)
            )

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_doPost_shouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBody() =
        runAsBlockingTest {
            val errorMessage = "bodyIsEmpty : The body within the request is empty"
            val expectedException = AccessCheckoutException(errorMessage)

            val jsonResponse = removeWhitespace(
                """{
                                "errorName": "bodyIsEmpty",
                                "message": "$errorMessage"
                            }"""
            )

            stubErrorResponse(
                responseCode = 400,
                responseBody = jsonResponse,
                message = errorMessage
            )
            given(clientErrorDeserializer.deserialize(jsonResponse)).willReturn(expectedException)

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_doPost_shouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBody() =
        runAsBlockingTest {
            stubErrorResponse(responseCode = 400, message = "Some Client Error")
            val errorMessage = "Error message was: Some Client Error"

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenServerCannotBeReached_doPost_shouldThrowAccessCheckoutExceptionWithCause() =
        runAsBlockingTest {
            given(serializer.serialize(testRequest)).willReturn(testRequestString)
            given(url.openConnection()).willThrow(ConnectException())

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertTrue { ace.cause is ConnectException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenSerializationException_doPost_shouldThrowAccessCheckoutExceptionWithCauseAndMessage() =
        runAsBlockingTest {
            given(serializer.serialize(testRequest)).willThrow(RuntimeException())

            try {
                httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "An exception was thrown when trying to establish a connection",
                    ace.message
                )
                assertTrue { ace.cause is java.lang.RuntimeException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenRedirectSentByServer_doPost_shouldFollowRedirect() = runAsBlockingTest {
        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse = TestResponse("abcdef")

        val relocatedUrl = "https://localhost:8443/someotherURL"
        val relocatedUrlMock: URL = mock()
        given(urlFactory.getURL(relocatedUrl)).willReturn(relocatedUrlMock)

        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(relocatedUrlMock.openConnection()).willReturn(httpsUrlConnection)

        given(httpsUrlConnection.responseCode).willReturn(301, 200)

        given(httpsUrlConnection.getHeaderField("Location")).willReturn(relocatedUrl)

        given(httpsUrlConnection.inputStream)
            .willReturn(testResponseAsString.byteInputStream())

        given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doPost(
            url,
            testRequest,
            newHashMap(mapOf(Pair("key", "value"))),
            serializer,
            deserializer
        )

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_doPost_shouldThrowAccessCheckoutHttpException() =
        runAsBlockingTest {
            stubResponse(responseCode = 301)

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(
                    url,
                    testRequest,
                    newHashMap(mapOf(Pair("key", "value"))),
                    serializer,
                    deserializer
                )
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Response from server was a redirect HTTP response code: 301 but did not include a Location header",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_doPost_shouldThrowAccessCheckoutHttpException() =
        runAsBlockingTest {
            val redirectResponseCode = 301

            given(url.openConnection()).willReturn(httpsUrlConnection)
            given(httpsUrlConnection.responseCode).willReturn(301)
            given(httpsUrlConnection.inputStream).willReturn(null)
            given(httpsUrlConnection.errorStream).willReturn(null)
            given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
            given(httpsUrlConnection.responseMessage).willReturn("")
            given(httpsUrlConnection.getHeaderField("Location")).willReturn("")

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doPost(
                    url,
                    testRequest,
                    newHashMap(mapOf(Pair("key", "value"))),
                    serializer,
                    deserializer
                )
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenValidRequest_doGet_shouldReturnSuccessfulResponse() = runAsBlockingTest {

        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse = TestResponse("abcdef")

        stubResponse(responseCode = 201, responseBody = testResponseAsString)

        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doGet(url, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenErrorWhenReadingResponseBody_doGet_shouldThrowAccessCheckoutException() =
        runAsBlockingTest {
            val errorMessage = "Some message"

            val inputStream = Mockito.mock(InputStream::class.java) {
                throw java.lang.RuntimeException(errorMessage)
            }

            given(url.openConnection()).willReturn(httpsUrlConnection)
            given(httpsUrlConnection.responseCode).willReturn(201)
            given(httpsUrlConnection.inputStream).willReturn(inputStream)
            given(httpsUrlConnection.errorStream).willReturn(inputStream)
            given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
            given(httpsUrlConnection.responseMessage).willReturn("")

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(errorMessage, ace.message)
                assertTrue { ace.cause is RuntimeException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttpStatusCodeBelow200_doGet_shouldThrowException() = runAsBlockingTest {
        val errorMessage = "A server error occurred when trying to make the request"

        stubErrorResponse(responseCode = 100, message = "")

        try {
            httpsClient.doGet(url, deserializer)
            fail("Expected exception but got none")
        } catch (ace: AccessCheckoutException) {
            assertEquals(errorMessage, ace.message)
        } catch (ex: Exception) {
            fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
        }
    }

    @Test
    fun givenHttp500Error_doGet_shouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBody() =
        runAsBlockingTest {
            stubErrorResponse(responseCode = 500)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals("A server error occurred when trying to make the request", ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp500Error_doGet_shouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponse() =
        runAsBlockingTest {
            stubErrorResponse(
                responseCode = 500,
                responseBody = "Some exception occurred",
                message = "Some http message"
            )

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Error message was: Some http message. Error response was: Some exception occurred",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp400ErrorWithResponseBody_doGet_shouldThrowAccessCheckoutExceptionWithErrorResponseFromServer() =
        runAsBlockingTest {
            val errorMessage = "The json body provided does not match the expected schema"
            val responseBody = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "fieldHasInvalidValue",
                                        "message": "Identity is invalid",
                                        "jsonPath": "${'$'}.identity"
                                    }
                                ]
                            }"""

            stubErrorResponse(
                responseCode = 400,
                responseBody = responseBody,
                message = errorMessage
            )

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Error message was: $errorMessage. Error response was: ${
                        responseBody.replace(
                            "\n",
                            ""
                        )
                    }", ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_doGet_shouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBody() =
        runAsBlockingTest {
            val errorMessage = "Cannot deserialize empty string"

            stubErrorResponse(responseCode = 400, responseBody = "", message = errorMessage)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals("Error message was: $errorMessage", ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_doGet_shouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBody() =
        runAsBlockingTest {
            stubErrorResponse(responseCode = 400, message = "Some Client Error")

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals("Error message was: Some Client Error", ace.message)
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenServerCannotBeReached_doGet_shouldThrowAccessCheckoutExceptionWithCause() =
        runAsBlockingTest {
            given(url.openConnection()).willThrow(ConnectException())

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertTrue { ace.cause is ConnectException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenDeserializationException_doGet_shouldThrowAccessCheckoutExceptionWithCauseAndMessage() =
        runAsBlockingTest {
            val responseBody = "{}"
            stubResponse(responseCode = 201, responseBody = responseBody)

            given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "An exception was thrown when trying to establish a connection",
                    ace.message
                )
                assertTrue { ace.cause is java.lang.RuntimeException }
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenRedirectSentByServer_ThenShouldFollowRedirectOnGet() = runAsBlockingTest {
        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )
        val testResponse = TestResponse("abcdef")
        val redirectToUrl = "https://localhost:8443/someotherURL"
        val redirectToUrlMock = mock<URL>()

        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(redirectToUrlMock.openConnection()).willReturn(httpsUrlConnection)
        given(httpsUrlConnection.responseCode).willReturn(301, 200)
        given(httpsUrlConnection.inputStream)
            .willReturn(testResponseAsString.byteInputStream())
        given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
        given(httpsUrlConnection.getHeaderField("Location")).willReturn(redirectToUrl)

        given(urlFactory.getURL(redirectToUrl)).willReturn(redirectToUrlMock)

        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doGet(url, deserializer)

        verify(urlFactory).getURL(redirectToUrl)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_doGet_shouldThrowAccessCheckoutHttpException() =
        runAsBlockingTest {
            val redirectResponseCode = 301

            given(url.openConnection()).willReturn(httpsUrlConnection)
            given(httpsUrlConnection.responseCode).willReturn(redirectResponseCode)

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_doGet_shouldThrowAccessCheckoutHttpException() =
        coroutinesTestRule.testDispatcher.runAsBlockingTest {
            stubResponse(responseCode = 301)
            given(httpsUrlConnection.getHeaderField("Location")).willReturn("")

            given(serializer.serialize(testRequest)).willReturn(testRequestString)

            try {
                httpsClient.doGet(url, deserializer)
                fail("Expected exception but got none")
            } catch (ace: AccessCheckoutException) {
                assertEquals(
                    "Response from server was a redirect HTTP response code: 301 but did not include a Location header",
                    ace.message
                )
            } catch (ex: Exception) {
                fail("Expected AccessCheckoutException but got " + ex.javaClass.simpleName)
            }
        }

    private fun stubErrorResponse(
        url: URL = this.url,
        responseCode: Int,
        responseBody: String? = null,
        message: String? = null
    ) {
        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(httpsUrlConnection.responseCode).willReturn(responseCode)
        given(httpsUrlConnection.errorStream).willReturn(responseBody?.byteInputStream())
        given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
        given(httpsUrlConnection.responseMessage).willReturn(message)
    }

    private fun stubResponse(
        url: URL = this.url,
        responseCode: Int,
        responseBody: String? = null
    ) {
        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(httpsUrlConnection.responseCode).willReturn(responseCode)

        if (responseBody != null) {
            given(httpsUrlConnection.inputStream).willReturn(responseBody.byteInputStream())
        } else {
            given(httpsUrlConnection.inputStream).willReturn(null)
        }

        given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
    }
}

data class TestRequest(val property: String) : Serializable
data class TestResponse(val property: String)

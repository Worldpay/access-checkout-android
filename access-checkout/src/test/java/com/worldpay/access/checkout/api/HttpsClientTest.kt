package com.worldpay.access.checkout.api

import com.google.common.collect.Maps.newHashMap
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.removeWhitespace
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.Serializable
import java.net.ConnectException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times

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

    @Before
    fun setup() {
        urlFactory = mock()
        deserializer = mock()
        serializer = mock()
        clientErrorDeserializer = mock()
        url = mock()
        httpsClient = HttpsClient(urlFactory, clientErrorDeserializer)
        httpsUrlConnection = mock()
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponseOnPost() {
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

        val response = httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

        assertEquals(testResponse, response)
        verify(httpsUrlConnection).setRequestProperty("key", "value")
    }

    @Test
    fun givenErrorWhenReadingResponseBody_ThenShouldThrowAccessCheckoutExceptionOnPost() {
        val errorMessage = "Some message"

        val inputStream = Mockito.mock(InputStream::class.java) {
            throw java.lang.RuntimeException(errorMessage)
        }

        stubResponse(responseCode = 201)
        given(httpsUrlConnection.inputStream).willReturn(inputStream)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(errorMessage, exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBodyOnPost() {
        val errorMessage = "A server error occurred when trying to make the request"

        stubErrorResponse(responseCode = 500, message = "")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun shouldThrowExceptionWhenStatusCodeIsBelow200ForPost() {
        val errorMessage = "A server error occurred when trying to make the request"

        stubErrorResponse(responseCode = 100, message = "")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnPost() {
        stubErrorResponse(responseCode = 500, responseBody = "Some exception occurred", message = "Some http message")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals("Error message was: Some http message. Error response was: Some exception occurred", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithResponseBody_ThenShouldThrowAccessCheckoutExceptionWithErrorResponseFromServerOnPost() {
        val errorMessage = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema"
        val expectedException = AccessCheckoutException(errorMessage)
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

        stubErrorResponse(responseCode = 400, responseBody = responseBody, message = errorMessage)
        given(clientErrorDeserializer.deserialize(responseBody)).willReturn(expectedException)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(expectedException, exception)
    }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_ThenShouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBodyOnPost() {
        val errorMessage = "bodyIsEmpty : The body within the request is empty"
        val expectedException = AccessCheckoutException(errorMessage)

        val jsonResponse = removeWhitespace(
            """{
                                "errorName": "bodyIsEmpty",
                                "message": "$errorMessage"
                            }"""
        )

        stubErrorResponse(responseCode = 400, responseBody = jsonResponse, message = errorMessage)
        given(clientErrorDeserializer.deserialize(jsonResponse)).willReturn(expectedException)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(expectedException, exception)
    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnPost() {
        stubErrorResponse(responseCode = 400, message = "Some Client Error")
        val expectedException = AccessCheckoutException("Error message was: Some Client Error")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(expectedException, exception)
    }

    @Test
    fun givenServerCannotBeReached_ThenShouldThrowAccessCheckoutExceptionWithCauseOnPost() {
        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(url.openConnection()).willThrow(ConnectException())

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertTrue(exception.cause is ConnectException)
    }

    @Test
    fun givenSerializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnPost() {
        given(serializer.serialize(testRequest)).willThrow(RuntimeException())

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals("An exception was thrown when trying to establish a connection", exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun givenDeserializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnPost() {
        val responseBody = "{}"
        stubResponse(responseCode = 201, responseBody = responseBody)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals("An exception was thrown when trying to establish a connection", exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun givenRedirectSentByServer_ThenShouldFollowRedirectOnPost() {
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

        val response = httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        stubResponse(responseCode = 301)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: 301 but did not include a Location header", exception.message)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        val redirectResponseCode = 301

        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(httpsUrlConnection.responseCode).willReturn(301)
        given(httpsUrlConnection.inputStream).willReturn(null)
        given(httpsUrlConnection.errorStream).willReturn(null)
        given(httpsUrlConnection.outputStream).willReturn(ByteArrayOutputStream())
        given(httpsUrlConnection.responseMessage).willReturn("")
        given(httpsUrlConnection.getHeaderField("Location")).willReturn("")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header", exception.message)
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponseOnGet() {

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
    fun givenErrorWhenReadingResponseBody_ThenShouldThrowAccessCheckoutExceptionOnGet() {
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

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals(errorMessage, exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun shouldThrowExceptionWhenStatusCodeIsBelow200ForGet() {
        val errorMessage = "A server error occurred when trying to make the request"

        stubErrorResponse(responseCode = 100, message = "")

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBodyOnGet() {
        stubErrorResponse(responseCode = 500)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("A server error occurred when trying to make the request", exception.message)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnGet() {
        stubErrorResponse(responseCode = 500, responseBody = "Some exception occurred", message = "Some http message")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: Some http message. Error response was: Some exception occurred", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithResponseBody_ThenShouldThrowAccessCheckoutExceptionWithErrorResponseFromServerOnGet() {
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

        stubErrorResponse(responseCode = 400, responseBody = responseBody, message = errorMessage)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: $errorMessage. Error response was: ${responseBody.replace("\n", "")}", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_ThenShouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBodyOnGet() {
        val errorMessage = "Cannot deserialize empty string"

        stubErrorResponse(responseCode = 400, responseBody = "", message = errorMessage)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: $errorMessage", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnGet() {
        stubErrorResponse(responseCode = 400, message = "Some Client Error")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: Some Client Error", exception.message)
    }

    @Test
    fun givenServerCannotBeReached_ThenShouldThrowAccessCheckoutExceptionWithCauseOnGet() {
        given(url.openConnection()).willThrow(ConnectException())

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertTrue(exception.cause is ConnectException)
    }

    @Test
    fun givenDeserializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnGet() {
        val responseBody = "{}"
        stubResponse(responseCode = 201, responseBody = responseBody)

        given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("An exception was thrown when trying to establish a connection", exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun givenRedirectSentByServer_ThenShouldFollowRedirectOnGet() {
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
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        val redirectResponseCode = 301

        given(url.openConnection()).willReturn(httpsUrlConnection)
        given(httpsUrlConnection.responseCode).willReturn(redirectResponseCode)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header", exception.message)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        stubResponse(responseCode = 301)
        given(httpsUrlConnection.getHeaderField("Location")).willReturn("")

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: 301 but did not include a Location header", exception.message)
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

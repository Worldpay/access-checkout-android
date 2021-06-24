package com.worldpay.access.checkout.api

import com.google.common.collect.Maps.newHashMap
import com.nhaarman.mockitokotlin2.mock
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.testutils.removeWhitespace
import java.io.InputStream
import java.io.Serializable
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito

class HttpsClientTest {

    private lateinit var urlFactory: URLFactory

    private lateinit var httpsClient: HttpsClient

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
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponseOnPost() {

        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse = TestResponse("abcdef")

        val httpURLConnection = stubResponse(buildMockedResponse(201, testResponseAsString, ""))
        assertTrue(httpURLConnection.requestProperties.isEmpty())

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

        assertEquals(testResponse, response)
        assertEquals(1, httpURLConnection.requestProperties.size)
        assertEquals("value", httpURLConnection.requestProperties["key"]?.get(0))
    }

    @Test
    fun givenErrorWhenReadingResponseBody_ThenShouldThrowAccessCheckoutExceptionOnPost() {
        val errorMessage = "Some message"

        val inputStream = Mockito.mock(InputStream::class.java) {
            throw java.lang.RuntimeException(errorMessage)
        }

        stubResponse(MockedResponse(201, inputStream, ""))

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

        stubResponse(buildMockedResponse(500, null, null))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(errorMessage, exception.message)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnPost() {
        stubResponse(buildMockedResponse(500, "Some exception occurred", "Some http message"))

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

        stubResponse(buildMockedResponse(400, responseBody, errorMessage))
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

        stubResponse(buildMockedResponse(400, jsonResponse, errorMessage))
        given(clientErrorDeserializer.deserialize(jsonResponse)).willReturn(expectedException)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
        }

        assertEquals(expectedException, exception)
    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnPost() {
        stubResponse(MockedResponse(400, null, "Some Client Error"))
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
        stubResponse(buildMockedResponse(201, responseBody, ""))

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
        val mockHttpRedirectURLConnection = MockHttpsURLConnection(
            url,
            MockedResponse(
                301, null, "",
                mutableMapOf(
                    Pair(
                        "Location",
                        relocatedUrl
                    )
                )
            )
        )
        val mockHttpSuccessURLConnection = MockHttpsURLConnection(url, MockedResponse(200, testResponseAsString.byteInputStream(), ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)
        given(relocatedUrlMock.openConnection()).willReturn(mockHttpSuccessURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        val redirectResponseCode = 301

        val mockHttpRedirectURLConnection = MockHttpsURLConnection(url, MockedResponse(redirectResponseCode, null, ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header", exception.message)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        val redirectResponseCode = 301

        val mockHttpRedirectURLConnection = MockHttpsURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location", ""))))

        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

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

        stubResponse(buildMockedResponse(201, testResponseAsString, ""))

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

        stubResponse(MockedResponse(201, inputStream, ""))

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals(errorMessage, exception.message)
        assertTrue(exception.cause is java.lang.RuntimeException)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBodyOnGet() {
        stubResponse(buildMockedResponse(500, null, null))

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("A server error occurred when trying to make the request", exception.message)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnGet() {
        stubResponse(buildMockedResponse(500, "Some exception occurred", "Some http message"))

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

        stubResponse(buildMockedResponse(400, responseBody, errorMessage))

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: $errorMessage. Error response was: ${responseBody.replace("\n", "")}", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_ThenShouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBodyOnGet() {
        val errorMessage = "Cannot deserialize empty string"

        stubResponse(buildMockedResponse(400, "", errorMessage))

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Error message was: $errorMessage", exception.message)
    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnGet() {
        stubResponse(MockedResponse(400, null, "Some Client Error"))

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
        stubResponse(buildMockedResponse(201, responseBody, ""))

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

        val relocatedUrl = "https://localhost:8443/someotherURL"
        val relocatedUrlMock: URL = mock()
        given(urlFactory.getURL(relocatedUrl)).willReturn(relocatedUrlMock)
        val mockHttpRedirectURLConnection = MockHttpsURLConnection(
            url,
            MockedResponse(
                301, null, "",
                mutableMapOf(
                    Pair(
                        "Location",
                        relocatedUrl
                    )
                )
            )
        )
        val mockHttpSuccessURLConnection = MockHttpsURLConnection(url, MockedResponse(200, testResponseAsString.byteInputStream(), ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)
        given(relocatedUrlMock.openConnection()).willReturn(mockHttpSuccessURLConnection)

        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpsClient.doGet(url, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        val redirectResponseCode = 301

        val mockHttpRedirectURLConnection = MockHttpsURLConnection(url, MockedResponse(redirectResponseCode, null, ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header", exception.message)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        val redirectResponseCode = 301

        val mockHttpRedirectURLConnection = MockHttpsURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location", ""))))

        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        val exception = assertFailsWith<AccessCheckoutException> {
            httpsClient.doGet(url, deserializer)
        }

        assertEquals("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header", exception.message)
    }

    private fun buildMockedResponse(
        responseCode: Int,
        responseBody: String?,
        message: String?
    ): MockedResponse {
        return MockedResponse(responseCode, responseBody?.byteInputStream(), message)
    }

    private fun stubResponse(response: MockedResponse): HttpURLConnection {
        val mockHttpURLConnection = MockHttpsURLConnection(url, response)
        given(url.openConnection()).willReturn(mockHttpURLConnection)
        return mockHttpURLConnection
    }
}

data class TestRequest(val property: String) : Serializable
data class TestResponse(val property: String)

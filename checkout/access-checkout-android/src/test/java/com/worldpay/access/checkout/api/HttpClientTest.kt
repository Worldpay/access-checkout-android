package com.worldpay.access.checkout.api

import com.google.common.collect.Maps.newHashMap
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.api.serialization.Serializer
import com.worldpay.access.checkout.testutils.mock
import com.worldpay.access.checkout.testutils.removeWhitespace
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.Mockito
import java.io.InputStream
import java.io.Serializable
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertTrue


class HttpClientTest {

    private lateinit var urlFactory: URLFactory

    private lateinit var httpClient: HttpClient

    private lateinit var deserializer: Deserializer<TestResponse>

    private lateinit var serializer: Serializer<TestRequest>

    private lateinit var clientErrorDeserializer: Deserializer<AccessCheckoutClientError>

    private val testRequest = TestRequest("abc")
    private val testRequestString = "{ \"property\": \"${testRequest.property}\" }"

    @Mock
    private lateinit var url: URL

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Before
    fun setup() {
        urlFactory = mock()
        deserializer = mock()
        serializer = mock()
        clientErrorDeserializer = mock()
        url = mock()
        httpClient = HttpClient(urlFactory, clientErrorDeserializer)
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponseOnPost() {

        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse  = TestResponse("abcdef")

        val httpURLConnection = stubResponse(buildMockedResponse(201, testResponseAsString, ""))
        assertTrue(httpURLConnection.requestProperties.isEmpty())

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

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

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(java.lang.RuntimeException::class.java))
        expectedException.expectMessage(errorMessage)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBodyOnPost() {
        val errorMessage = "A server error occurred when trying to make the request"
        expectedException.expect(AccessCheckoutError::class.java)
        expectedException.expectMessage(errorMessage)

        stubResponse(buildMockedResponse(500, null, null))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnPost() {
        expectedException.expect(AccessCheckoutError::class.java)
        expectedException.expectMessage("Some http message")
        expectedException.expectMessage("Some exception occurred")

        stubResponse(buildMockedResponse(500, "Some exception occurred", "Some http message"))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
    }

    @Test
    fun givenHttp400ErrorWithResponseBody_ThenShouldThrowAccessCheckoutExceptionWithErrorResponseFromServerOnPost() {

        val errorMessage = "The json body provided does not match the expected schema"
        val responseBody =
            removeWhitespace("""{
                        "errorName": "bodyDoesNotMatchSchema",
                        "message": "The json body provided does not match the expected schema",
                        "validationErrors": [
                            {
                                "errorName": "fieldHasInvalidValue",
                                "message": "Identity is invalid",
                                "jsonPath": "${'$'}.identity"
                            }
                        ]
                    }""")


        expectedException.expect(AccessCheckoutClientError::class.java)
        expectedException.expectMessage(errorMessage)

        stubResponse(buildMockedResponse(400, responseBody, errorMessage))
        given(clientErrorDeserializer.deserialize(responseBody)).willReturn(AccessCheckoutClientError(Error.BODY_DOES_NOT_MATCH_SCHEMA, errorMessage))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)

    }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_ThenShouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBodyOnPost() {
        val errorMessage = "The body within the request is empty"

        expectedException.expect(AccessCheckoutClientError::class.java)
        expectedException.expectMessage(errorMessage)

        val jsonResponse =  removeWhitespace("""{
                                "errorName": "bodyIsEmpty",
                                "message": "$errorMessage"
                            }""")

        stubResponse(buildMockedResponse(400, jsonResponse, errorMessage))
        given(clientErrorDeserializer.deserialize(jsonResponse)).willReturn(AccessCheckoutClientError(Error.BODY_IS_EMPTY, errorMessage))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)

    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnPost() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Error message was: Some Client Error")

        stubResponse(MockedResponse(400, null, "Some Client Error"))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)
    }

    @Test
    fun givenServerCannotBeReached_ThenShouldThrowAccessCheckoutExceptionWithCauseOnPost() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(ConnectException::class.java))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(url.openConnection()).willThrow(ConnectException())

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)

    }

    @Test
    fun givenSerializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnPost() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(RuntimeException::class.java))
        expectedException.expectMessage("An exception was thrown when trying to establish a connection")

        given(serializer.serialize(testRequest)).willThrow(RuntimeException())

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)

    }

    @Test
    fun givenDeserializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnPost() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(RuntimeException::class.java))
        expectedException.expectMessage("An exception was thrown when trying to establish a connection")

        val responseBody = "{}"
        stubResponse(buildMockedResponse(201, responseBody, ""))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

        httpClient.doPost(url, testRequest, newHashMap(), serializer, deserializer)

    }

    @Test
    fun givenRedirectSentByServer_ThenShouldFollowRedirectOnPost() {
        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse  = TestResponse("abcdef")


        val relocatedUrl = "http://localhost/someotherURL"
        val relocatedUrlMock: URL = mock()
        given(urlFactory.getURL(relocatedUrl)).willReturn(relocatedUrlMock)
        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location",
            relocatedUrl
        ))))
        val mockHttpSuccessURLConnection = MockHttpURLConnection(url, MockedResponse(200, testResponseAsString.byteInputStream(), ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)
        given(relocatedUrlMock.openConnection()).willReturn(mockHttpSuccessURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)
        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        val redirectResponseCode = 301

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header")

        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(redirectResponseCode, null, ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnPost() {
        val redirectResponseCode = 301

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header")

        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location", ""))))

        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doPost(url, testRequest, newHashMap(mapOf(Pair("key", "value"))), serializer, deserializer)
    }

    @Test
    fun givenValidRequest_shouldReturnSuccessfulResponseOnGet() {

        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse  = TestResponse("abcdef")

        stubResponse(buildMockedResponse(201, testResponseAsString, ""))

        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpClient.doGet(url, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenErrorWhenReadingResponseBody_ThenShouldThrowAccessCheckoutExceptionOnGet() {
        val errorMessage = "Some message"

        val inputStream = Mockito.mock(InputStream::class.java) {
            throw java.lang.RuntimeException(errorMessage)
        }

        stubResponse(MockedResponse(201, inputStream, ""))

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(java.lang.RuntimeException::class.java))
        expectedException.expectMessage(errorMessage)

        httpClient.doGet(url, deserializer)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithDefaultMessageWhenNoResponseBodyOnGet() {
        expectedException.expect(AccessCheckoutError::class.java)
        expectedException.expectMessage("A server error occurred when trying to make the request")

        stubResponse(buildMockedResponse(500, null, null))

        httpClient.doGet(url, deserializer)
    }

    @Test
    fun givenHttp500Error_ThenShouldThrowAccessCheckoutErrorWithMessageFromServerWhenBodyInResponseOnGet() {
        expectedException.expect(AccessCheckoutError::class.java)
        expectedException.expectMessage("Some http message")
        expectedException.expectMessage("Some exception occurred")

        stubResponse(buildMockedResponse(500, "Some exception occurred", "Some http message"))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doGet(url, deserializer)
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


        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage(errorMessage)

        stubResponse(buildMockedResponse(400, responseBody, errorMessage))

        httpClient.doGet(url, deserializer)

    }

    @Test
    fun givenHttp400ErrorWithNoResponseBody_ThenShouldThrowAccessCheckoutExceptionWithoutAnyErrorResponseBodyOnGet() {
        val errorMessage = "Cannot deserialize empty string"

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage(errorMessage)

        stubResponse(buildMockedResponse(400, "", errorMessage))


        httpClient.doGet(url, deserializer)
    }

    @Test
    fun givenHttp400ErrorWithEmptyResponseData_ThenShouldThrowAccessCheckoutHttpExceptionWithoutAnyErrorResponseBodyOnGet() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Error message was: Some Client Error")

        stubResponse(MockedResponse(400, null, "Some Client Error"))

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doGet(url, deserializer)
    }

    @Test
    fun givenServerCannotBeReached_ThenShouldThrowAccessCheckoutExceptionWithCauseOnGet() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(ConnectException::class.java))

        given(url.openConnection()).willThrow(ConnectException())

        httpClient.doGet(url, deserializer)

    }

    @Test
    fun givenDeserializationException_ThenShouldThrowAccessCheckoutExceptionWithCauseAndMessageOnGet() {
        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectCause(instanceOf(RuntimeException::class.java))
        expectedException.expectMessage("An exception was thrown when trying to establish a connection")

        val responseBody = "{}"
        stubResponse(buildMockedResponse(201, responseBody, ""))

        given(deserializer.deserialize(responseBody)).willThrow(RuntimeException())

        httpClient.doGet(url, deserializer)

    }

    @Test
    fun givenRedirectSentByServer_ThenShouldFollowRedirectOnGet() {
        val testResponseAsString = removeWhitespace(
            """{
                "property": "abcdef"
                }"""
        )

        val testResponse  = TestResponse("abcdef")

        val relocatedUrl = "http://localhost/someotherURL"
        val relocatedUrlMock: URL = mock()
        given(urlFactory.getURL(relocatedUrl)).willReturn(relocatedUrlMock)
        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location",
            relocatedUrl
        ))))
        val mockHttpSuccessURLConnection = MockHttpURLConnection(url, MockedResponse(200, testResponseAsString.byteInputStream(), ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)
        given(relocatedUrlMock.openConnection()).willReturn(mockHttpSuccessURLConnection)

        given(deserializer.deserialize(testResponseAsString)).willReturn(testResponse)

        val response = httpClient.doGet(url, deserializer)

        assertEquals(testResponse, response)
    }

    @Test
    fun givenRedirectSentWithNoLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        val redirectResponseCode = 301

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header")

        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(redirectResponseCode, null, ""))
        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doGet(url, deserializer)
    }

    @Test
    fun givenRedirectSentWithEmptyLocationHeaderByServer_ThenShouldThrowAccessCheckoutHttpExceptionOnGet() {
        val redirectResponseCode = 301

        expectedException.expect(AccessCheckoutHttpException::class.java)
        expectedException.expectMessage("Response from server was a redirect HTTP response code: $redirectResponseCode but did not include a Location header")

        val mockHttpRedirectURLConnection = MockHttpURLConnection(url, MockedResponse(301, null, "", mutableMapOf(Pair("Location", ""))))

        given(url.openConnection()).willReturn(mockHttpRedirectURLConnection)

        given(serializer.serialize(testRequest)).willReturn(testRequestString)

        httpClient.doGet(url, deserializer)
    }

    private fun buildMockedResponse(
        responseCode: Int,
        responseBody: String?,
        message: String?
    ): MockedResponse {
        return MockedResponse(responseCode, responseBody?.byteInputStream(), message)
    }

    private fun stubResponse(response: MockedResponse): HttpURLConnection {
        val mockHttpURLConnection = MockHttpURLConnection(url, response)
        given(url.openConnection()).willReturn(mockHttpURLConnection)
        return mockHttpURLConnection
    }

}

data class TestRequest(val property: String) : Serializable
data class TestResponse(val property: String)
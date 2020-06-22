package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ClientErrorDeserializerTest {

    private val clientErrorDeserializer = ClientErrorDeserializer()

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun givenEmptyResponseThenShouldThrowDeserializationException() {
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Cannot deserialize empty string")

        clientErrorDeserializer.deserialize("")
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Cannot interpret json: $json")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Missing property: 'errorName'")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = """{
                        "errorName": "methodNotAllowed"
                    }"""
        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Missing property: 'message'")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = """{
                        "errorName": 23,
                        "message": "Requested method is not allowed"
                    }"""


        expectedException.expect(AccessCheckoutException::class.java)
        expectedException.expectMessage("Invalid property type: 'errorName', expected 'String'")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenValidMethodNotAllowedErrorJsonThenShouldSuccessfullyDeserializeToException() {
        val jsonResponse =
            """{
                "errorName": "methodNotAllowed",
                "message": "Requested method is not allowed"
            }"""

        val deserializedResponse = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedErrorObject = AccessCheckoutException("methodNotAllowed : Requested method is not allowed")

        assertEquals(expectedErrorObject, deserializedResponse)
    }

    @Test
    fun givenValidBodySchemaErrorJsonWithMultipleBrokenRulesThenShouldSuccessfullyDeserializeToExceptionWithCompleteValidationRulesList() {
        val jsonResponse =
            """{
                "errorName": "bodyDoesNotMatchSchema",
                "message": "The json body provided does not match the expected schema",
                "validationErrors": [
                    {
                        "errorName": "integerIsTooLarge",
                        "message": "Card expiry year must be less than 9999",
                        "jsonPath": "${'$'}.cardExpiryDate.year"
                    },
                    {
                        "errorName": "fieldHasInvalidValue",
                        "message": "Card number must be numeric",
                        "jsonPath": "${'$'}.cardNumber"
                    },
                    {
                        "errorName": "stringIsTooShort",
                        "message": "Card number is too short - must be between 10 & 19 digits",
                        "jsonPath": "${'$'}.cardNumber"
                    },
                    {
                        "errorName": "fieldHasInvalidValue",
                        "message": "Identity is invalid",
                        "jsonPath": "${'$'}.identity"
                    }
                ]
            }"""

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
        val expectedValidationRuleList =
            listOf(
                ValidationRule(
                    "integerIsTooLarge",
                    "Card expiry year must be less than 9999",
                    "\$.cardExpiryDate.year"
                ),
                ValidationRule(
                    "fieldHasInvalidValue",
                    "Card number must be numeric",
                    "\$.cardNumber"
                ),
                ValidationRule(
                    "stringIsTooShort",
                    "Card number is too short - must be between 10 & 19 digits",
                    "\$.cardNumber"
                ),
                ValidationRule("fieldHasInvalidValue", "Identity is invalid", "\$.identity")
            )


        val expectedErrorObject = AccessCheckoutException(
            message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
            validationRules = expectedValidationRuleList
        )

        assertEquals(expectedErrorObject, deserializedError)
    }

    @Test
    fun givenValidBodyNotJsonErrorJsonThenShouldSuccessfullyDeserializeToException() {
        val jsonResponse =
            """{
                "errorName": "bodyIsNotJson",
                "message": "The body within the request is not valid json"
            }"""


        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)


        val expectedErrorObject = AccessCheckoutException("bodyIsNotJson : The body within the request is not valid json")

        assertEquals(expectedErrorObject, deserializedError)
    }

    @Test
    fun givenValidBodySchemaErrorWithLuhnInvalidCardErrorThenShouldSuccessfullyDeserializeToException() {
        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "${'$'}.cardNumber"
                                    }
                                ]
                            }"""

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException =
            AccessCheckoutException(
                message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                validationRules = listOf(
                    ValidationRule(
                        "panFailedLuhnCheck",
                        "The identified field contains a PAN that has failed the Luhn check.",
                        "\$.cardNumber"
                    )
                )
            )

        assertEquals(expectedException, deserializedError)

    }

    @Test
    fun givenValidBodySchemaErrorWithUnknownFieldErrorThenShouldSuccessfullyDeserializeToException() {
        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "aaa": "bbb",
                                "validationErrors": [
                                    {
                                        "errorName": "unrecognizedField",
                                        "message": "Field is not recognized",
                                        "jsonPath": "${'$'}.aaa"
                                    }
                                ]
                            }"""

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException =
            AccessCheckoutException(
                message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
                validationRules = listOf(
                    ValidationRule(
                        "unrecognizedField",
                        "Field is not recognized",
                        "\$.aaa"
                    )
                )
            )

        assertEquals(expectedException, deserializedError)

    }

    @Test
    fun givenEmptyBodyErrorResponseThenShouldSuccessfullyDeserializeException() {
        val jsonResponse = """{
                                "errorName": "bodyIsEmpty",
                                "message": "The body within the request is empty"
                            }"""

        val expectedException = AccessCheckoutException("bodyIsEmpty : The body within the request is empty")

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenUndocumentedErrorResponseThenShouldSuccessfullyDeserializeUnknownError() {
        val jsonResponse = """{
                                "errorName": "this is an undocumented error",
                                "message": "this error should be documented"
                            }"""

        val expectedException = AccessCheckoutException("this is an undocumented error : this error should be documented")

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenMalformatedErrorWithUnknownFields_whenDeserializing_shouldThrowException() {
        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "aaa": "bbb",
                                "validationErrors": [
                                    {
                                        "errorName": "xxxxx",
                                        "message": "yyyy",
                                        "jsonPath": "${'$'}.aaa"
                                    }
                                ]
                            }"""

        val error = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException = AccessCheckoutException(
            message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
            validationRules = listOf(ValidationRule("xxxxx", "yyyy", "${'$'}.aaa"))
        )

        assertEquals(expectedException, error)
    }

    @Test
    fun givenEmptyValidationRules_whenDeserializing_shouldThrowException() {

        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": []
                            }"""

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException = AccessCheckoutException("bodyDoesNotMatchSchema : The json body provided does not match the expected schema")

        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenInvalidValidationRules_whenDeserializing_shouldThrowException() {
        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "aaa": "bbb",
                                "validationErrors": [
                                    {
                                        "errorName": "xxxxx",
                                        "message": "yyyy",
                                        "jsonPath": "${'$'}.aaa",
                                        "asdasd":"aaa"
                                    }
                                ]
                            }"""

        val error = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException = AccessCheckoutException(
            message = "bodyDoesNotMatchSchema : The json body provided does not match the expected schema",
            validationRules = listOf(ValidationRule("xxxxx", "yyyy", "${'$'}.aaa"))
        )

        assertEquals(expectedException, error)
    }

}

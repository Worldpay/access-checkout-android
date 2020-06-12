package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.AccessCheckoutException.*
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
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot deserialize empty string")

        clientErrorDeserializer.deserialize("")
    }

    @Test
    fun givenBadJsonStringThenShouldThrowDeserializationException() {
        val json = "abc"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Cannot interpret json: $json")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingObjectThenShouldThrowDeserializationException() {
        val json = "{ }"
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'errorName'")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithMissingPropertyThenShouldThrowDeserializationException() {
        val json = """{
                        "errorName": "methodNotAllowed"
                    }"""
        expectedException.expect(AccessCheckoutDeserializationException::class.java)
        expectedException.expectMessage("Missing property: 'message'")

        clientErrorDeserializer.deserialize(json)
    }

    @Test
    fun givenJsonStringWithInvalidStringTypeThenShouldThrowDeserializationException() {
        val json = """{
                        "errorName": 23,
                        "message": "Requested method is not allowed"
                    }"""


        expectedException.expect(AccessCheckoutDeserializationException::class.java)
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


        val expectedErrorObject = AccessCheckoutClientError(Error.METHOD_NOT_ALLOWED, "Requested method is not allowed")

        assertEquals(expectedErrorObject, deserializedResponse)
    }

    @Test
    fun givenValidBodySchemaErrorJsonThenShouldSuccessfullyDeserializeToException() {
        val expectedNameToValidationRuleMap = mapOf(
            Pair("unrecognizedField", ValidationRuleName.UNRECOGNIZED_FIELD),
            Pair("fieldHasInvalidValue", ValidationRuleName.FIELD_HAS_INVALID_VALUE),
            Pair("panFailedLuhnCheck", ValidationRuleName.PAN_FAILED_LUHN_CHECK),
            Pair("fieldIsMissing", ValidationRuleName.FIELD_IS_MISSING),
            Pair("stringIsTooShort", ValidationRuleName.STRING_IS_TOO_SHORT),
            Pair("stringIsTooLong", ValidationRuleName.STRING_IS_TOO_LONG),
            Pair("fieldMustBeInteger", ValidationRuleName.FIELD_MUST_BE_INTEGER),
            Pair("integerIsTooSmall", ValidationRuleName.INTEGER_IS_TOO_SMALL),
            Pair("integerIsTooLarge", ValidationRuleName.INTEGER_IS_TOO_LARGE),
            Pair("fieldMustBeNumber", ValidationRuleName.FIELD_MUST_BE_NUMBER),

            Pair("fieldMustBeString", ValidationRuleName.FIELD_MUST_BE_STRING),
            Pair("fieldMustBeBoolean", ValidationRuleName.FIELD_MUST_BE_BOOLEAN),
            Pair("fieldMustBeObject", ValidationRuleName.FIELD_MUST_BE_OBJECT),
            Pair("fieldMustBeArray", ValidationRuleName.FIELD_MUST_BE_ARRAY),
            Pair("fieldIsNull", ValidationRuleName.FIELD_IS_NULL),
            Pair("fieldIsEmpty", ValidationRuleName.FIELD_IS_EMPTY),
            Pair("fieldIsNotAllowed", ValidationRuleName.FIELD_IS_NOT_ALLOWED),
            Pair("numberIsTooSmall", ValidationRuleName.NUMBER_IS_TOO_SMALL),
            Pair("numberIsTooLarge", ValidationRuleName.NUMBER_IS_TOO_LARGE),
            Pair("stringFailedRegexCheck", ValidationRuleName.STRING_FAILED_REGEX_CHECK),
            Pair("dateHasInvalidFormat", ValidationRuleName.DATE_HAS_INVALID_FORMAT)
        )

        expectedNameToValidationRuleMap.forEach { (name, validationRule) ->
            run {
                val message = "Some message"
                val jsonPath = "\$.somePath"
                val jsonResponse = getErrorResponseAsString(name, message, jsonPath)

                val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
                val expectedValidationRuleList = listOf(ValidationRule(validationRule, message, jsonPath))

                val expectedErrorObject = AccessCheckoutClientError(
                    Error.BODY_DOES_NOT_MATCH_SCHEMA,
                    "The json body provided does not match the expected schema",
                    expectedValidationRuleList
                )

                assertEquals(expectedErrorObject, deserializedError)
            }
        }
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
                    ValidationRuleName.INTEGER_IS_TOO_LARGE,
                    "Card expiry year must be less than 9999",
                    "\$.cardExpiryDate.year"
                ),
                ValidationRule(
                    ValidationRuleName.FIELD_HAS_INVALID_VALUE,
                    "Card number must be numeric",
                    "\$.cardNumber"
                ),
                ValidationRule(
                    ValidationRuleName.STRING_IS_TOO_SHORT,
                    "Card number is too short - must be between 10 & 19 digits",
                    "\$.cardNumber"
                ),
                ValidationRule(ValidationRuleName.FIELD_HAS_INVALID_VALUE, "Identity is invalid", "\$.identity")
            )


        val expectedErrorObject = AccessCheckoutClientError(
            Error.BODY_DOES_NOT_MATCH_SCHEMA,
            "The json body provided does not match the expected schema",
            expectedValidationRuleList
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


        val expectedErrorObject = AccessCheckoutClientError(
            Error.BODY_IS_NOT_JSON,
            "The body within the request is not valid json"
        )

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
            AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(
                    ValidationRule(
                        ValidationRuleName.PAN_FAILED_LUHN_CHECK,
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
            AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                listOf(
                    ValidationRule(
                        ValidationRuleName.UNRECOGNIZED_FIELD,
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

        val expectedException = AccessCheckoutClientError(Error.BODY_IS_EMPTY, "The body within the request is empty")

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenUndocumentedErrorResponseThenShouldSuccessfullyDeserializeUnknownError() {

        val jsonResponse = """{
                                "errorName": "this is an undocumented error",
                                "message": "this error should be documented"
                            }"""

        val expectedException = AccessCheckoutClientError(Error.UNKNOWN_ERROR, "this error should be documented")

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)
        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenMalformatedErrorWithUnknownFields_whenDeserializing_shouldThrowException() {

        expectedException.expect(AccessCheckoutClientError::class.java)

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

        clientErrorDeserializer.deserialize(jsonResponse)

    }

    @Test
    fun givenEmptyValidationRules_whenDeserializing_shouldThrowException() {

        val jsonResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": []
                            }"""

        val deserializedError = clientErrorDeserializer.deserialize(jsonResponse)

        val expectedException =
            AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                "The json body provided does not match the expected schema",
                null
            )

        assertEquals(expectedException, deserializedError)
    }

    @Test
    fun givenInvalidValidationRules_whenDeserializing_shouldThrowException() {

        expectedException.expect(AccessCheckoutClientError::class.java)

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

        clientErrorDeserializer.deserialize(jsonResponse)

    }

    private fun getErrorResponseAsString(errorName: String, message: String, jsonPath: String): String {
        return """{
                    "errorName": "bodyDoesNotMatchSchema",
                    "message": "The json body provided does not match the expected schema",
                    "validationErrors": [
                        {
                            "errorName": "$errorName",
                            "message": "$message",
                            "jsonPath": "$jsonPath"
                        }
                    ]
                }"""
    }
}

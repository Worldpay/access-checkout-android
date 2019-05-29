package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class AccessCheckoutExceptionTest {

    private val documentedErrorList = listOf(
        "bodyIsNotJson",
        "bodyIsEmpty",
        "bodyDoesNotMatchSchema",
        "resourceNotFound",
        "endpointNotFound",
        "methodNotAllowed",
        "unsupportedAcceptHeader",
        "unsupportedContentType",
        "internalErrorOccurred",
        "unknownError"
    )

    private val documentedValidationRuleList = listOf(
        "unrecognizedField",
        "fieldHasInvalidValue",
        "panFailedLuhnCheck",
        "fieldIsMissing",
        "stringIsTooShort",
        "stringIsTooLong",
        "fieldMustBeInteger",
        "integerIsTooSmall",
        "integerIsTooLarge",
        "fieldMustBeNumber",

        "fieldMustBeString",
        "fieldMustBeBoolean",
        "fieldMustBeObject",
        "fieldMustBeArray",
        "fieldIsNull",
        "fieldIsEmpty",
        "fieldIsNotAllowed",
        "numberIsTooSmall",
        "numberIsTooLarge",
        "stringFailedRegexCheck",
        "dateHasInvalidFormat"
    )

    private val schemaMessage = "The json body provided does not match the expected schema"
    private val ruleMessage = "rule message"
    private val rulePath = "rule path"

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun getRuleForName() =
        documentedValidationRuleList.forEach {
            assertTrue(it == AccessCheckoutException.getRuleForName(it).errorName)
        }

    @Test
    fun getErrorForName() =
        documentedErrorList.forEach {
            assertTrue(it == AccessCheckoutException.getErrorForName(it).errorName)
        }

    @Test
    fun getUnknownError() {
        val unknownRuleName = "xxxxxxxxx"
        try {
            AccessCheckoutException.getRuleForName(unknownRuleName)
            fail("Should not reach this point")
        } catch (ex: AccessCheckoutClientError) {
            assert(ex.error == Error.UNKNOWN_ERROR)
        } catch (ex: java.lang.Exception) {
            fail("Should not reach this point")
        }
    }

    @Test
    fun getRulesFromError() {
        val validationRuleList = mutableListOf<ValidationRule>()
        documentedValidationRuleList.forEach {
            validationRuleList.add(
                ValidationRule(
                    AccessCheckoutException.getRuleForName(it),
                    ruleMessage,
                    rulePath
                )
            )
        }

        val customException =
            AccessCheckoutClientError(
                Error.BODY_DOES_NOT_MATCH_SCHEMA,
                schemaMessage,
                validationRuleList
            )

        assertEquals(schemaMessage, customException.message)

        customException.validationRules?.forEach { rule ->
            documentedValidationRuleList.any { it == rule.errorName.errorName }
            assertTrue(rule.message == ruleMessage)
            assertTrue(rule.jsonPath == rulePath)
        }
    }

    @Test
    fun errorCodeTest() {
        assertEquals(400, Error.BODY_IS_NOT_JSON.errorCode)
        assertEquals(400, Error.BODY_IS_EMPTY.errorCode)
        assertEquals(400, Error.BODY_DOES_NOT_MATCH_SCHEMA.errorCode)
        assertEquals(404, Error.RESOURCE_NOT_FOUND.errorCode)
        assertEquals(404, Error.ENDPOINT_NOT_FOUND.errorCode)
        assertEquals(405, Error.METHOD_NOT_ALLOWED.errorCode)
        assertEquals(406, Error.UNSUPPORTED_ACCEPT_HEADER.errorCode)
        assertEquals(415, Error.UNSUPPORTED_CONTENT_TYPE.errorCode)
        assertEquals(500, Error.INTERNAL_ERROR_OCCURRED.errorCode)
        assertEquals(500, Error.UNKNOWN_ERROR.errorCode)
    }

    @Test
    fun testExceptionInternalPropertyGetter() {
        val internalEx = Exception()
        val ex = AccessCheckoutError("dummy error", internalEx)
        assertEquals(internalEx, ex.cause)

        val ex2 = AccessCheckoutDeserializationException("dummy error", internalEx)
        assertEquals(internalEx, ex2.cause)
    }

}
package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.exception.RequestError
import com.worldpay.access.checkout.api.exception.RequestErrorProvider
import com.worldpay.access.checkout.api.exception.ValidationRule
import com.worldpay.access.checkout.api.exception.ValidationRuleErrorProvider
import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertFailsWith

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
            assertTrue(it == ValidationRuleErrorProvider.getRuleForName(it).errorName)
        }

    @Test
    fun getErrorForName() =
        documentedErrorList.forEach {
            assertTrue(it == RequestErrorProvider.getErrorForName(it).errorName)
        }

    @Test
    fun getUnknownError() {
        val unknownRuleName = "xxxxxxxxx"

        val exception = assertFailsWith<AccessCheckoutException> {
            ValidationRuleErrorProvider.getRuleForName(unknownRuleName)
        }

        assertEquals(AccessCheckoutException("unknown rule name xxxxxxxxx"), exception)
    }

    @Test
    fun getRulesFromError() {
        val validationRuleList = mutableListOf<ValidationRule>()
        documentedValidationRuleList.forEach {
            validationRuleList.add(
                ValidationRule(
                    ValidationRuleErrorProvider.getRuleForName(it),
                    ruleMessage,
                    rulePath
                )
            )
        }

        val customException =
            AccessCheckoutException(
                message = "bodyDoesNotMatchSchema : $schemaMessage",
                validationRules = validationRuleList
            )

        assertEquals("bodyDoesNotMatchSchema : $schemaMessage", customException.message)

        customException.validationRules.forEach { rule ->
            documentedValidationRuleList.any { it == rule.errorName.errorName }
            assertTrue(rule.message == ruleMessage)
            assertTrue(rule.jsonPath == rulePath)
        }
    }

    @Test
    fun errorCodeTest() {
        assertEquals(400, RequestError.BODY_IS_NOT_JSON.errorCode)
        assertEquals(400, RequestError.BODY_IS_EMPTY.errorCode)
        assertEquals(400, RequestError.BODY_DOES_NOT_MATCH_SCHEMA.errorCode)
        assertEquals(404, RequestError.RESOURCE_NOT_FOUND.errorCode)
        assertEquals(404, RequestError.ENDPOINT_NOT_FOUND.errorCode)
        assertEquals(405, RequestError.METHOD_NOT_ALLOWED.errorCode)
        assertEquals(406, RequestError.UNSUPPORTED_ACCEPT_HEADER.errorCode)
        assertEquals(415, RequestError.UNSUPPORTED_CONTENT_TYPE.errorCode)
        assertEquals(500, RequestError.INTERNAL_ERROR_OCCURRED.errorCode)
        assertEquals(500, RequestError.UNKNOWN_ERROR.errorCode)
    }

    @Test
    fun testExceptionInternalPropertyGetter() {
        val internalEx = Exception()
        val ex = AccessCheckoutException("dummy error", internalEx)
        assertEquals(internalEx, ex.cause)

        val ex2 = AccessCheckoutException("dummy error", internalEx)
        assertEquals(internalEx, ex2.cause)
    }

}

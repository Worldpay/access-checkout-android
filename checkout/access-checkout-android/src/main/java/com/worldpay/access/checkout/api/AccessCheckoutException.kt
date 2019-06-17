package com.worldpay.access.checkout.api

import com.worldpay.access.checkout.api.AccessCheckoutException.Error.*
import com.worldpay.access.checkout.api.AccessCheckoutException.ValidationRuleName.*

/**
 * [AccessCheckoutException] to indicate that something went wrong during the request for a session state
 */
sealed class AccessCheckoutException : RuntimeException() {

    companion object {

        @JvmStatic
        fun getRuleForName(ruleName: String): ValidationRuleName =
            when (ruleName) {
                UNRECOGNIZED_FIELD.errorName -> UNRECOGNIZED_FIELD
                FIELD_HAS_INVALID_VALUE.errorName -> FIELD_HAS_INVALID_VALUE
                PAN_FAILED_LUHN_CHECK.errorName -> PAN_FAILED_LUHN_CHECK
                FIELD_IS_MISSING.errorName -> FIELD_IS_MISSING
                STRING_IS_TOO_SHORT.errorName -> STRING_IS_TOO_SHORT
                STRING_IS_TOO_LONG.errorName -> STRING_IS_TOO_LONG
                FIELD_MUST_BE_INTEGER.errorName -> FIELD_MUST_BE_INTEGER
                INTEGER_IS_TOO_SMALL.errorName -> INTEGER_IS_TOO_SMALL
                INTEGER_IS_TOO_LARGE.errorName -> INTEGER_IS_TOO_LARGE
                FIELD_MUST_BE_NUMBER.errorName -> FIELD_MUST_BE_NUMBER

                FIELD_MUST_BE_STRING.errorName -> FIELD_MUST_BE_STRING
                FIELD_MUST_BE_BOOLEAN.errorName -> FIELD_MUST_BE_BOOLEAN
                FIELD_MUST_BE_OBJECT.errorName -> FIELD_MUST_BE_OBJECT
                FIELD_MUST_BE_ARRAY.errorName -> FIELD_MUST_BE_ARRAY
                FIELD_IS_NULL.errorName -> FIELD_IS_NULL
                FIELD_IS_EMPTY.errorName -> FIELD_IS_EMPTY
                FIELD_IS_NOT_ALLOWED.errorName -> FIELD_IS_NOT_ALLOWED
                NUMBER_IS_TOO_SMALL.errorName -> NUMBER_IS_TOO_SMALL
                NUMBER_IS_TOO_LARGE.errorName -> NUMBER_IS_TOO_LARGE
                STRING_FAILED_REGEX_CHECK.errorName -> STRING_FAILED_REGEX_CHECK
                DATE_HAS_INVALID_FORMAT.errorName -> DATE_HAS_INVALID_FORMAT
                else -> throw AccessCheckoutClientError(UNKNOWN_ERROR, "unknown error")
            }

        @JvmStatic
        fun getErrorForName(errorName: String): Error =
            when (errorName) {
                BODY_IS_NOT_JSON.errorName -> BODY_IS_NOT_JSON
                BODY_IS_EMPTY.errorName -> BODY_IS_EMPTY
                BODY_DOES_NOT_MATCH_SCHEMA.errorName -> BODY_DOES_NOT_MATCH_SCHEMA
                RESOURCE_NOT_FOUND.errorName -> RESOURCE_NOT_FOUND
                ENDPOINT_NOT_FOUND.errorName -> ENDPOINT_NOT_FOUND
                METHOD_NOT_ALLOWED.errorName -> METHOD_NOT_ALLOWED
                UNSUPPORTED_ACCEPT_HEADER.errorName -> UNSUPPORTED_ACCEPT_HEADER
                UNSUPPORTED_CONTENT_TYPE.errorName -> UNSUPPORTED_CONTENT_TYPE
                INTERNAL_ERROR_OCCURRED.errorName -> INTERNAL_ERROR_OCCURRED
                else -> UNKNOWN_ERROR
            }
    }

    data class AccessCheckoutClientError(
        val error: Error,
        override val message: String?,
        val validationRules: List<ValidationRule>? = null
    ) : AccessCheckoutException()

    data class AccessCheckoutError(
        override val message: String?,
        override val cause: Exception? = null
    ) : AccessCheckoutException()

    data class AccessCheckoutDeserializationException(
        override val message: String?,
        override val cause: Exception? = null
    ) : AccessCheckoutException()

    data class AccessCheckoutDiscoveryException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AccessCheckoutException()

    data class AccessCheckoutConfigurationException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AccessCheckoutException()

    data class AccessCheckoutHttpException(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AccessCheckoutException()



    enum class Error(val errorCode: Int, val errorName: String) {
        BODY_IS_NOT_JSON(400, "bodyIsNotJson"),
        BODY_IS_EMPTY(400, "bodyIsEmpty"),
        BODY_DOES_NOT_MATCH_SCHEMA(400, "bodyDoesNotMatchSchema"),
        RESOURCE_NOT_FOUND(404, "resourceNotFound"),
        ENDPOINT_NOT_FOUND(404, "endpointNotFound"),
        METHOD_NOT_ALLOWED(405, "methodNotAllowed"),
        UNSUPPORTED_ACCEPT_HEADER(406, "unsupportedAcceptHeader"),
        UNSUPPORTED_CONTENT_TYPE(415, "unsupportedContentType"),
        INTERNAL_ERROR_OCCURRED(500, "internalErrorOccurred"),
        UNKNOWN_ERROR(500, "unknownError")
    }

    data class ValidationRule(val errorName: ValidationRuleName, val message: String, val jsonPath: String)
    enum class ValidationRuleName(val errorName: String) {
        UNRECOGNIZED_FIELD("unrecognizedField"),
        FIELD_HAS_INVALID_VALUE("fieldHasInvalidValue"),
        PAN_FAILED_LUHN_CHECK("panFailedLuhnCheck"),
        FIELD_IS_MISSING("fieldIsMissing"),
        STRING_IS_TOO_SHORT("stringIsTooShort"),
        STRING_IS_TOO_LONG("stringIsTooLong"),
        FIELD_MUST_BE_INTEGER("fieldMustBeInteger"),
        INTEGER_IS_TOO_SMALL("integerIsTooSmall"),
        INTEGER_IS_TOO_LARGE("integerIsTooLarge"),
        FIELD_MUST_BE_NUMBER("fieldMustBeNumber"),

        FIELD_MUST_BE_STRING("fieldMustBeString"),
        FIELD_MUST_BE_BOOLEAN("fieldMustBeBoolean"),
        FIELD_MUST_BE_OBJECT("fieldMustBeObject"),
        FIELD_MUST_BE_ARRAY("fieldMustBeArray"),
        FIELD_IS_NULL("fieldIsNull"),
        FIELD_IS_EMPTY("fieldIsEmpty"),
        FIELD_IS_NOT_ALLOWED("fieldIsNotAllowed"),
        NUMBER_IS_TOO_SMALL("numberIsTooSmall"),
        NUMBER_IS_TOO_LARGE("numberIsTooLarge"),
        STRING_FAILED_REGEX_CHECK("stringFailedRegexCheck"),
        DATE_HAS_INVALID_FORMAT("dateHasInvalidFormat")
    }
}


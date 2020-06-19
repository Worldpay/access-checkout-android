package com.worldpay.access.checkout.api.exception

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException

object ValidationRuleErrorProvider {

    fun getRuleForName(ruleName : String) : ValidationRuleName =
        when (ruleName) {
            ValidationRuleName.UNRECOGNIZED_FIELD.errorName -> ValidationRuleName.UNRECOGNIZED_FIELD
            ValidationRuleName.FIELD_HAS_INVALID_VALUE.errorName -> ValidationRuleName.FIELD_HAS_INVALID_VALUE
            ValidationRuleName.PAN_FAILED_LUHN_CHECK.errorName -> ValidationRuleName.PAN_FAILED_LUHN_CHECK
            ValidationRuleName.FIELD_IS_MISSING.errorName -> ValidationRuleName.FIELD_IS_MISSING
            ValidationRuleName.STRING_IS_TOO_SHORT.errorName -> ValidationRuleName.STRING_IS_TOO_SHORT
            ValidationRuleName.STRING_IS_TOO_LONG.errorName -> ValidationRuleName.STRING_IS_TOO_LONG
            ValidationRuleName.FIELD_MUST_BE_INTEGER.errorName -> ValidationRuleName.FIELD_MUST_BE_INTEGER
            ValidationRuleName.INTEGER_IS_TOO_SMALL.errorName -> ValidationRuleName.INTEGER_IS_TOO_SMALL
            ValidationRuleName.INTEGER_IS_TOO_LARGE.errorName -> ValidationRuleName.INTEGER_IS_TOO_LARGE
            ValidationRuleName.FIELD_MUST_BE_NUMBER.errorName -> ValidationRuleName.FIELD_MUST_BE_NUMBER

            ValidationRuleName.FIELD_MUST_BE_STRING.errorName -> ValidationRuleName.FIELD_MUST_BE_STRING
            ValidationRuleName.FIELD_MUST_BE_BOOLEAN.errorName -> ValidationRuleName.FIELD_MUST_BE_BOOLEAN
            ValidationRuleName.FIELD_MUST_BE_OBJECT.errorName -> ValidationRuleName.FIELD_MUST_BE_OBJECT
            ValidationRuleName.FIELD_MUST_BE_ARRAY.errorName -> ValidationRuleName.FIELD_MUST_BE_ARRAY
            ValidationRuleName.FIELD_IS_NULL.errorName -> ValidationRuleName.FIELD_IS_NULL
            ValidationRuleName.FIELD_IS_EMPTY.errorName -> ValidationRuleName.FIELD_IS_EMPTY
            ValidationRuleName.FIELD_IS_NOT_ALLOWED.errorName -> ValidationRuleName.FIELD_IS_NOT_ALLOWED
            ValidationRuleName.NUMBER_IS_TOO_SMALL.errorName -> ValidationRuleName.NUMBER_IS_TOO_SMALL
            ValidationRuleName.NUMBER_IS_TOO_LARGE.errorName -> ValidationRuleName.NUMBER_IS_TOO_LARGE
            ValidationRuleName.STRING_FAILED_REGEX_CHECK.errorName -> ValidationRuleName.STRING_FAILED_REGEX_CHECK
            ValidationRuleName.DATE_HAS_INVALID_FORMAT.errorName -> ValidationRuleName.DATE_HAS_INVALID_FORMAT
            else -> throw AccessCheckoutException("unknown rule name $ruleName")
        }

}

data class ValidationRule(val errorName : ValidationRuleName, val message : String, val jsonPath : String)

enum class ValidationRuleName(val errorName : String) {
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

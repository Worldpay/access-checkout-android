package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import com.worldpay.access.checkout.client.api.exception.ValidationRule
import org.json.JSONObject

internal class ClientErrorDeserializer : Deserializer<AccessCheckoutException>() {

    override fun deserialize(json: String): AccessCheckoutException {
        return super.deserialize(json) {
            val root = JSONObject(json)

            val clientErrorName = toStringProperty(root, "errorName")
            val message = toStringProperty(root, "message")
            var clientValidationRuleList: List<ValidationRule> = emptyList()

            if (root.has("validationErrors")) {
                clientValidationRuleList = deserializeValidationRules(root)
            }

            AccessCheckoutException(
                message = "$clientErrorName : $message",
                validationRules = clientValidationRuleList
            )
        }
    }

    private fun deserializeValidationRules(root: JSONObject): List<ValidationRule> {
        val validationErrorArr = fetchArray(root, "validationErrors")
        val valErrSize = validationErrorArr.length()
        if (valErrSize > 0) {
            val clientValidationRuleList = mutableListOf<ValidationRule>()

            for (i in 0 until valErrSize) {
                val vErr = validationErrorArr.getJSONObject(i)
                clientValidationRuleList.add(getValidationRuleFromJson(vErr))
            }
            return clientValidationRuleList.toList()
        }

        return emptyList()
    }

    private fun getValidationRuleFromJson(vErr: JSONObject): ValidationRule {
        val validationRuleErrorName = toStringProperty(vErr, "errorName")
        val validationErrorMessage = toStringProperty(vErr, "message")
        val validationErrorPath = toStringProperty(vErr, "jsonPath")

        return ValidationRule(
            validationRuleErrorName,
            validationErrorMessage,
            validationErrorPath
        )
    }
}

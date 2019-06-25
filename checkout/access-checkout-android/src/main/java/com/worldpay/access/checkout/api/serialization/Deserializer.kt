package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutDeserializationException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.KClass

internal typealias Parser<T> = (String) -> T

internal abstract class Deserializer<T> {

    abstract fun deserialize(json: String): T

    protected fun deserialize(json: String, parser: Parser<T>): T {
        if (json.isEmpty()) {
            throw AccessCheckoutDeserializationException("Cannot deserialize empty string")
        }

        return try {
            parser(json)
        } catch (jsonException: JSONException) {
            throw AccessCheckoutDeserializationException("Cannot interpret json: $json", jsonException)
        }
    }

    protected fun toStringProperty(obj: JSONObject, field: String): String = toProperty(obj, field, String::class)

    protected fun toBooleanProperty(obj: JSONObject, field: String): Boolean = toProperty(obj, field, Boolean::class)

    protected fun toOptionalStringProperty(obj: JSONObject, field: String): String? =
        toOptionalProperty(obj, field, String::class)

    protected fun toOptionalIntProperty(obj: JSONObject, field: String): Int? =
        toOptionalProperty(obj, field, Int::class)

    protected fun fetchOptionalArray(obj: JSONObject, field: String): JSONArray? {
        return try {
            fetchArray(obj, field)
        } catch (ex: AccessCheckoutDeserializationException) {
            return null
        }
    }

    protected fun fetchArray(obj: JSONObject, field: String): JSONArray {
        return try {
            obj.getJSONArray(field)
        } catch (ex: JSONException) {
            throw AccessCheckoutDeserializationException("Missing array: '$field'", ex)
        }
    }

    protected fun fetchOptionalObject(obj: JSONObject, field: String): JSONObject? {
        return obj.optJSONObject(field)
    }

    protected fun fetchObject(obj: JSONObject, field: String): JSONObject {
        return fetchOrElseThrow(obj.optJSONObject(field), field, "object")
    }

    private fun <T : Any> toProperty(obj: JSONObject, field: String, clazz: KClass<out T>): T {
        return toOptionalProperty(obj, field, clazz)
            ?: throw AccessCheckoutDeserializationException("Missing property: '$field'")
    }

    private fun <T : Any> toOptionalProperty(obj: JSONObject, field: String, clazz: KClass<out T>): T? {
        return try {
            val fetchProperty = fetchProperty(obj, field)
            clazz.javaObjectType.cast(fetchProperty)
        } catch (ex: ClassCastException) {
            throw AccessCheckoutDeserializationException(
                "Invalid property type: '$field', expected '${clazz.simpleName}'",
                ex
            )
        } catch (ex: AccessCheckoutDeserializationException) {
            null
        }
    }

    private fun fetchProperty(obj: JSONObject, field: String): Any {
        return try {
            obj.get(field)
        } catch (ex: JSONException) {
            throw AccessCheckoutDeserializationException("Missing property: '$field'", ex)
        }
    }

    private fun <T> fetchOrElseThrow(obj: T?, field: String, type: String): T {
        return obj ?: throw AccessCheckoutDeserializationException("Missing $type: '$field'")
    }
}
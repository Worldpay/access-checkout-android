package com.worldpay.access.checkout.api.serialization

import com.worldpay.access.checkout.client.api.exception.AccessCheckoutException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.KClass

internal typealias Parser<T> = (String) -> T

/**
 * This class is responsible for deserialising a json [String] into a generic type of [T]
 */
internal abstract class Deserializer<T> {

    /**
     * Deserialises [json] [String] into the type of [T]
     */
    abstract fun deserialize(json: String): T

    /**
     * Deserialises [json] [String] into the type of [T] using a given [parser]
     *
     * @param [json] the json [String] to deserialise
     * @param [parser] the [Parser] to use for deserialisation
     */
    protected fun deserialize(json: String, parser: Parser<T>): T {
        if (json.isEmpty()) {
            throw AccessCheckoutException("Cannot deserialize empty string")
        }

        return try {
            parser(json)
        } catch (jsonException: JSONException) {
            throw AccessCheckoutException(
                "Cannot interpret json: $json",
                jsonException
            )
        }
    }

    protected open fun toStringProperty(obj: JSONObject, field: String): String = toProperty(obj, field, String::class)

    protected fun fetchOptionalArray(obj: JSONObject, field: String): JSONArray? {
        return try {
            fetchArray(obj, field)
        } catch (ex: AccessCheckoutException) {
            return null
        }
    }

    protected fun fetchArray(obj: JSONObject, field: String): JSONArray {
        return try {
            obj.getJSONArray(field)
        } catch (ex: JSONException) {
            throw AccessCheckoutException("Missing array: '$field'", ex)
        }
    }

    protected fun fetchObject(obj: JSONObject, field: String): JSONObject {
        return fetchOrElseThrow(obj.optJSONObject(field), field, "object")
    }

    protected fun <T : Any> toProperty(obj: JSONObject, field: String, clazz: KClass<out T>): T {
        return toOptionalProperty(obj, field, clazz)
            ?: throw AccessCheckoutException("Missing property: '$field'")
    }

    protected fun <T : Any> toOptionalProperty(obj: JSONObject, field: String, clazz: KClass<out T>): T? {
        return try {
            val fetchProperty = fetchProperty(obj, field)
            clazz.javaObjectType.cast(fetchProperty)
        } catch (ex: ClassCastException) {
            throw AccessCheckoutException(
                "Invalid property type: '$field', expected '${clazz.simpleName}'",
                ex
            )
        } catch (ex: AccessCheckoutException) {
            null
        }
    }

    private fun fetchProperty(obj: JSONObject, field: String): Any {
        return try {
            obj.get(field)
        } catch (ex: JSONException) {
            throw AccessCheckoutException("Missing property: '$field'", ex)
        }
    }

    private fun <T> fetchOrElseThrow(obj: T?, field: String, type: String): T {
        return obj ?: throw AccessCheckoutException("Missing $type: '$field'")
    }
}

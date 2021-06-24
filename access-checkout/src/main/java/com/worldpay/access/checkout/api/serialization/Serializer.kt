package com.worldpay.access.checkout.api.serialization

import java.io.Serializable

/**
 * This interface should be implemented when an instance of type [T] is to be serialised into a [String]
 *
 * The instance of [T] should implement [Serializable]
 */
internal interface Serializer<T : Serializable> {

    /**
     * Serialises the instance of type [T] to a [String]
     *
     * @param[instance] The instance that needs to be serialised to a [String]
     */
    fun serialize(instance: T): String
}

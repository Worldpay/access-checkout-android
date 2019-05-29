package com.worldpay.access.checkout.api.serialization

import java.io.Serializable

internal interface Serializer<T: Serializable> {

    fun serialize(instance: T): String
}
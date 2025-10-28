package com.worldpay.access.checkout.validation.cardbin.api

import java.util.concurrent.ConcurrentHashMap

internal class CardBinCacheManager {
    private val cache = ConcurrentHashMap<String, CardBinResponse>()

    fun getCacheKey(key: String): String = key.take(12)

    fun getCachedResponse(key: String): CardBinResponse? = cache[key]

    fun putInCache(key: String, value: CardBinResponse) {
        cache[key] = value
    }
}
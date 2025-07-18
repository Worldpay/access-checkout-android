package com.worldpay.access.checkout.cardbin.api.client

import com.worldpay.access.checkout.cardbin.api.response.CardBinResponse
import java.util.concurrent.ConcurrentHashMap

internal class CardBinCacheManager {
    private val cache = ConcurrentHashMap<String, CardBinResponse>()

    fun getCacheKey(key: String): String = key.take(12)

    fun getCachedResponse(key: String): CardBinResponse? = cache[key]

    fun putInCache(key: String, value: CardBinResponse) {
        cache[key] = value
    }
}
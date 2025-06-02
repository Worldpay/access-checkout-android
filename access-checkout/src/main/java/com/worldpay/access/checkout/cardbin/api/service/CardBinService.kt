package com.worldpay.access.checkout.cardbin.api.service

import android.util.Log
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan

internal class CardBinService() {

    fun getCardBrands(newCardBrand: RemoteCardBrand?, pan: String): List<RemoteCardBrand> {
        if (newCardBrand == null) {
            return emptyList()
        }

        //hard code the pan for now, will be remove in future work
        val hardCodedBrand = findBrandForPan("5555444433332222")
        if (hardCodedBrand != null) {
            val hardCodedBrands = listOf(newCardBrand, hardCodedBrand)
            Log.d(javaClass.simpleName, "Available brands for card: $hardCodedBrands")
            return hardCodedBrands
        }

        return listOf(newCardBrand)
    }
}

package com.worldpay.access.checkout.service

import android.util.Log
import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.validation.utils.ValidationUtil.findBrandForPan

internal class BrandService {
    private val requiredPanLengthForCardBrands = 12


    fun getCardBrands(newCardBrand: RemoteCardBrand?, pan: String): List<RemoteCardBrand> {
        if (newCardBrand == null) {
            return emptyList()
        }

        if (isPanRequiredLength(pan)) {
            val hardCodedBrand = findBrandForPan("5555444433332222")
            if (hardCodedBrand != null) {
                val hardCodedBrands = listOf(newCardBrand, hardCodedBrand)
                Log.d(javaClass.simpleName, "Available brands for card: $hardCodedBrands")
                return hardCodedBrands
            }
        }
        return listOf(newCardBrand)
    }

    private fun isPanRequiredLength(pan: String): Boolean {
        val formattedPan = pan.replace(" ", "").length
        return (formattedPan >= requiredPanLengthForCardBrands)
    }
}
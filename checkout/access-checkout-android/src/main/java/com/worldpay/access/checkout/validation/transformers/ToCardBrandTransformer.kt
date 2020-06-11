package com.worldpay.access.checkout.validation.transformers

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.api.configuration.RemoteCardBrandImage
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.client.validation.model.CardBrandImage

class ToCardBrandTransformer {

    fun transform(remoteCardBrand: RemoteCardBrand?): CardBrand? {
        if (remoteCardBrand == null) return null

        return CardBrand(
            name = remoteCardBrand.name,
            images = transformToCardBrandImages(remoteCardBrand.images)
        )
    }

    private fun transformToCardBrandImages(images: List<RemoteCardBrandImage>): List<CardBrandImage> {
        val cardBrandImages = mutableListOf<CardBrandImage>()
        for (image in images) {
            cardBrandImages.add(CardBrandImage(image.type, image.url))
        }
        return cardBrandImages
    }
}

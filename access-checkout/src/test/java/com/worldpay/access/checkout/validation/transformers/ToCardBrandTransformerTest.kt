package com.worldpay.access.checkout.validation.transformers

import com.worldpay.access.checkout.api.configuration.RemoteCardBrand
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Before
import org.junit.Test

class ToCardBrandTransformerTest {

    private lateinit var toCardBrandTransformer: ToCardBrandTransformer

    @Before
    fun setup() {
        toCardBrandTransformer = ToCardBrandTransformer()
    }

    @Test
    fun `should transform remote card brand to card brand`() {
        val actual = toCardBrandTransformer.transform(VISA_BRAND)

        assertNotNull(actual)

        assertEquals(VISA_BRAND.name, actual.name)

        assertEquals(VISA_BRAND.images[0].type, actual.images[0].type)
        assertEquals(VISA_BRAND.images[0].url, actual.images[0].url)

        assertEquals(VISA_BRAND.images[1].type, actual.images[1].type)
        assertEquals(VISA_BRAND.images[1].url, actual.images[1].url)
    }

    @Test
    fun `should transform remote card brand to card brand with no images`() {
        val brand = RemoteCardBrand("brand", emptyList(), VISA_BRAND.cvc, VISA_BRAND.pan)
        val actual = toCardBrandTransformer.transform(brand)

        assertNotNull(actual)

        assertEquals("brand", actual.name)
        assertEquals(0, actual.images.size)
    }

    @Test
    fun `should return null when null is passed into transformer`() {
        assertNull(toCardBrandTransformer.transform(null))
    }
}

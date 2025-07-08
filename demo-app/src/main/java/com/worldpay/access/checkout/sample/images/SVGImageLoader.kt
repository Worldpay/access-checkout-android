package com.worldpay.access.checkout.sample.images

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.client.validation.model.CardBrandImage
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.sample.ssl.client.TrustAllSSLSocketFactory
import com.worldpay.access.checkout.sample.ssl.client.TrustAllSSLSocketFactory.Companion.X509_TRUST_MANAGER
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException

/**
 * This class is responsible for fetching a remote SVG file and applying it to a target view
 */
class SVGImageLoader @JvmOverloads constructor(
    private val runOnUiThreadFunc: (Runnable) -> Unit,
    private val cacheDir: File?,
    private val client: OkHttpClient = buildDefaultClient(
        cacheDir
    ),
    private val svgImageRenderer: SVGImageRenderer = buildSvgImageRenderer(
        runOnUiThreadFunc
    )
) {
    companion object {
        @JvmStatic
        @Volatile
        private var INSTANCE: SVGImageLoader? = null

        /**
         * @param activity the current activity
         * @return an [SVGImageLoader] instance
         */
        @JvmStatic
        fun getInstance(activity: Activity): SVGImageLoader {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: SVGImageLoader(
                        activity::runOnUiThread,
                        activity.cacheDir
                    ).also { INSTANCE = it }
            }
        }

        private const val IMAGE_TYPE = "image/svg+xml"

        private fun buildDefaultClient(cacheDir: File?): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .sslSocketFactory(TrustAllSSLSocketFactory(), X509_TRUST_MANAGER)
            cacheDir?.let { builder.cache(Cache(cacheDir, 5 * 1024 * 1014)) }
            return builder
                .build()
        }

        private fun buildSvgImageRenderer(uiRunner: (Runnable) -> Unit): SVGImageRenderer {
            return SVGImageRendererImpl(uiRunner)
        }
    }

    /**
     * Fetches the appropriate SVG image for a [CardBrand] from a remotely hosted endpoint over HTTP,
     * and applies it to a target [ImageView]
     *
     * @param cardBrand the [CardBrand] to which to fetch the image for
     * @param target the target [ImageView] to apply the image to
     */
    fun fetchAndApplyCardLogo(cardBrand: CardBrand?, target: ImageView) {
        if (cardBrand == null) {
            setUnknownCardBrand(target)
            return
        }

        for (image in cardBrand.images) {
            if (image.type != IMAGE_TYPE) {
                continue
            }

            applyBrandImage(cardBrand.name, image, target)
            return // Stop looping after applying the first valid image
        }

        setUnknownCardBrand(target) // Fallback if no valid image is found
    }

    private fun applyBrandImage(brandName: String, image: CardBrandImage, target: ImageView) {
        Log.d("SVGImageLoader", "Requesting brand image: ${image.url}")

        val request = Request.Builder().url(image.url).build()
        val newCall = client.newCall(request)

        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    run {
                        Log.d("SVGImageLoader", "Received image for brand $brandName")
                        svgImageRenderer.renderImage(responseBody.byteStream(), target, brandName)
                    }
                }
            }
        })
    }

    private fun setUnknownCardBrand(target: ImageView) {
        runOnUiThreadFunc {
            Log.d("SVGImageLoader", "Applying card unknown logo to target view")
            target.setImageResource(R.drawable.card_unknown_logo)
            val resourceEntryName =
                target.resources.getResourceEntryName(R.drawable.card_unknown_logo)
            target.setTag(R.integer.card_tag, resourceEntryName)
        }
    }
}

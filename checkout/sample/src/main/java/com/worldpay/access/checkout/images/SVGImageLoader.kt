package com.worldpay.access.checkout.images

import android.app.Activity
import android.widget.ImageView
import com.worldpay.access.checkout.R
import com.worldpay.access.checkout.logging.AccessCheckoutLogger
import com.worldpay.access.checkout.logging.Logger
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.utils.CustomIdlingResourceCounter
import com.worldpay.access.checkout.views.PANLayout
import okhttp3.*
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
    ),
    private val logger: Logger = AccessCheckoutLogger()
) {
    private val idleResCounter = CustomIdlingResourceCounter("SvgImageLoader")

    companion object {

        @Volatile
        private var INSTANCE: SVGImageLoader? = null

        /**
         * @param activity the current activity
         * @return an [SVGImageLoader] instance
         */
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

        cardBrand?.let {
            val url = it.images?.find { image -> image.type == IMAGE_TYPE }?.url

            url?.let {
                idleResCounter.increment()
                val request = Request.Builder().url(url).build()
                val newCall = client.newCall(request)
                newCall.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {

                        response.body()?.let { responseBody ->
                            run {
                                svgImageRenderer.renderImage(responseBody.byteStream(), target, cardBrand.name)
                                idleResCounter.decrement()
                                idleResCounter.unregisterIdleResCounter()
                            }
                        }
                    }
                })
            }
        } ?: setUnknownCardBrand(target)
    }

    private fun setUnknownCardBrand(target: ImageView) {
        logger.debugLog("SVGImageLoader", "Applying card unknown logo to target view")
        target.setImageResource(R.drawable.card_unknown_logo)
        val resourceEntryName = target.resources.getResourceEntryName(R.drawable.card_unknown_logo)
        target.setTag(PANLayout.CARD_TAG, resourceEntryName)
    }

}

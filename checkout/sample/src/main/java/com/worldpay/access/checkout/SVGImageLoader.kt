package com.worldpay.access.checkout

import android.widget.ImageView
import com.worldpay.access.checkout.model.CardBrand
import okhttp3.*
import java.io.File
import java.io.IOException


class SVGImageLoader(
    private val runOnUiThreadFunc: (Runnable) -> Unit,
    private val cacheDir: File?,
    private val client: OkHttpClient = buildDefaultClient(cacheDir),
    private val svgImageRenderer: SVGImageRenderer = buildSvgImageRenderer(runOnUiThreadFunc)) {

    companion object {

        @Volatile private var INSTANCE: SVGImageLoader? = null

        fun getInstance(runOnUiThreadFunc: (Runnable) -> Unit, cacheDir: File? = null): SVGImageLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SVGImageLoader(runOnUiThreadFunc, cacheDir).also { INSTANCE = it }
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

    fun fetchAndApplyCardLogo(cardBrand: CardBrand?, target: ImageView) {

        cardBrand?.let {
            val url = it.images?.find { image -> image.type == IMAGE_TYPE }?.url

            url?.let {
                val request = Request.Builder().url(url).build()
                val newCall = client.newCall(request)
                newCall.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        response.body()?.let { responseBody ->
                            svgImageRenderer.renderImage(responseBody.byteStream(), target)
                        }
                    }
                })
            }
        } ?: setUnknownCardBrand(target)
    }

    private fun setUnknownCardBrand(target: ImageView) = target.setImageResource(R.drawable.card_unknown_logo)

}

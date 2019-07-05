package com.worldpay.access.checkout

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import com.worldpay.access.checkout.model.CardBrand
import okhttp3.*
import java.io.IOException


object SVGImageLoader {

    private const val IMAGE_TYPE = "image/svg+xml"

    private var httpClient: OkHttpClient? = null

    fun fetchAndApplyCardLogo(activity: Activity, cardBrand: CardBrand?, target: ImageView,
                              client: OkHttpClient = buildDefaultClient(activity),
                              svgImageRenderer: SVGImageRenderer = buildSvgImageRenderer(activity, target)) {
        if (httpClient == null) {
            httpClient = client
        }

        cardBrand?.let {
            val url = it.images?.find { image -> image.type == IMAGE_TYPE }?.url

            url?.let {
                val request = Request.Builder().url(url).build()
                httpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        response.body()?.let { responseBody ->
                            svgImageRenderer.renderImage(responseBody.byteStream())
                        }
                    }
                })
            }
        } ?: setUnknownCardBrand(target)
    }

    private fun setUnknownCardBrand(target: ImageView) = target.setImageResource(R.drawable.card_unknown_logo)

    private fun buildDefaultClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, 5 * 1024 * 1014))
            .build()
    }

    private fun buildSvgImageRenderer(activity: Activity, target: ImageView): SVGImageRenderer {
        return SVGImageRendererImpl(activity, target)
    }

}

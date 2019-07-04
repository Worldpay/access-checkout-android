package com.worldpay.access.checkout

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.worldpay.access.checkout.model.CardBrand
import okhttp3.*
import java.io.IOException




object ImageUtils {

    private var httpClient: OkHttpClient? = null

    fun applyCardLogo(activity: MainActivity, cardBrand: CardBrand?, target: ImageView) {
        if (httpClient == null) {
            httpClient = OkHttpClient.Builder()
                .cache(Cache(activity.cacheDir, 5 * 1024 * 1014))
                .build()
        }
        cardBrand?.let {
            val url = it.images?.find { image -> image.type == "image/svg+xml" }?.url

            url?.let {
                val request = Request.Builder().url(url).build()
                httpClient!!.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        target.setImageResource(R.drawable.card_unknown_logo)
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val stream = response.body()?.byteStream()

                        try {
                            val svg = SVG.getFromInputStream(stream)
                            val drawable = PictureDrawable(svg.renderToPicture())
                            activity.runOnUiThread {
                                target.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                                target.setImageDrawable(drawable)
                            }
                            stream?.close()
                        } catch (e: SVGParseException) {
                        }

                    }
                })
            }
        } ?: target.setImageResource(R.drawable.card_unknown_logo)
    }

}

package com.worldpay.access.checkout

import android.app.Activity
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.worldpay.access.checkout.logging.LoggingUtils
import java.io.InputStream

interface SVGImageRenderer {
    fun renderImage(inputStream: InputStream)
}

internal class SVGImageRendererImpl(private val activity: Activity, private val targetView: ImageView): SVGImageRenderer {

    override fun renderImage(inputStream: InputStream) {
        try {
            val svg = SVG.getFromInputStream(inputStream)
            val drawable = PictureDrawable(svg.renderToPicture(targetView.measuredWidth, targetView.measuredHeight))
            activity.runOnUiThread {
                targetView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                targetView.setImageDrawable(drawable)
            }
            inputStream.close()
        } catch (e: SVGParseException) {
            LoggingUtils.debugLog("SVGImageLoader", "Failed to parse SVG image: ${e.message}")
        }
    }
}
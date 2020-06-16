package com.worldpay.access.checkout.sample.images

import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.sample.R
import com.worldpay.access.checkout.util.logging.AccessCheckoutLogger
import com.worldpay.access.checkout.util.logging.Logger
import java.io.InputStream

interface SVGImageRenderer {
    fun renderImage(inputStream: InputStream, targetView: ImageView, brandName: String)
}

/**
 * This class is responsible for rendering an SVG image into a target view
 *
 * @param runOnUiThreadFunc the reference to a runOnUiThread function
 * @param logger the logging instance
 * @param svgWrapper the [SVGWrapper]
 */
class SVGImageRendererImpl(private val runOnUiThreadFunc: (Runnable) -> Unit,
                           private val logger: Logger = AccessCheckoutLogger(),
                           private val svgWrapper: SVGWrapper = SVGWrapper.svgWrapper
): SVGImageRenderer {

    /**
     * Renders a stream of SVG data into a target view
     *
     * @param inputStream the svg data
     * @param targetView the target [ImageView]
     * @param brandName the brand associated with this image
     */
    override fun renderImage(inputStream: InputStream, targetView: ImageView, brandName: String) {
        try {
            val svg = svgWrapper.getSVGFromInputStream(inputStream)
            val drawable = PictureDrawable(svg.renderToPicture(targetView.measuredWidth, targetView.measuredHeight))
            runOnUiThreadFunc(Runnable {
                logger.debugLog("SVGImageRendererImpl", "Applying $brandName logo to target view")
                targetView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                targetView.setImageDrawable(drawable)
                targetView.setTag(R.integer.card_tag, brandName)
            })
        } catch (e: Exception) {
            logger.errorLog("SVGImageRendererImpl", "Failed to parse SVG image: ${e.message}")
        }
    }
}

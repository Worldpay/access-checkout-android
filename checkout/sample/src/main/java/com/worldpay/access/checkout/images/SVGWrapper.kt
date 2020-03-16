package com.worldpay.access.checkout.images

import com.caverock.androidsvg.SVG
import java.io.InputStream

class SVGWrapper {

    companion object {
        @JvmStatic
        val svgWrapper = SVGWrapper()
    }

    fun getSVGFromInputStream(inputStream: InputStream): SVG {
        return SVG.getFromInputStream(inputStream)
    }

}
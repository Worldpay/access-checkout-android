package com.worldpay.access.checkout.images

import android.app.Activity
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.worldpay.access.checkout.logging.Logger
import com.worldpay.access.checkout.views.PANLayout
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito
import java.io.InputStream

class SVGImageRendererImplTest {

    private lateinit var svgImageRenderer: SVGImageRenderer
    private lateinit var activity: Activity
    private lateinit var target: ImageView
    private lateinit var logger: Logger
    private lateinit var svgWrapper: SVGWrapper
    private lateinit var runOnUiThreadFun: (Runnable) -> Unit

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        activity = Mockito.mock(Activity::class.java)
        target = Mockito.mock(ImageView::class.java)
        logger = Mockito.mock(Logger::class.java)
        svgWrapper = Mockito.mock(SVGWrapper::class.java)
        runOnUiThreadFun = Mockito.mock(Function1::class.java as Class<Function1<Runnable, Unit>>)
        svgImageRenderer =
            SVGImageRendererImpl(runOnUiThreadFun, logger, svgWrapper)
    }

    @Test
    fun shouldRenderImageIntoTargetView() {
        val inputStream = Mockito.mock(InputStream::class.java)
        val svg = Mockito.mock(SVG::class.java)

        given(target.measuredWidth).willReturn(1)
        given(target.measuredHeight).willReturn(1)
        given(svgWrapper.getSVGFromInputStream(inputStream)).willReturn(svg)
        given(svg.renderToPicture(1, 1)).willReturn(Mockito.mock(Picture::class.java))

        svgImageRenderer.renderImage(inputStream!!, target, "someName")

        val argumentCaptor = argumentCaptor<Runnable>()
        Mockito.verify(runOnUiThreadFun).invoke(argumentCaptor.capture())

        argumentCaptor.firstValue.run()

        Mockito.verify(target).setImageDrawable(ArgumentMatchers.any(PictureDrawable::class.java))
        Mockito.verify(target).setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        Mockito.verify(target).setTag(PANLayout.CARD_TAG, "someName")
    }

    @Test
    fun shouldLogExceptionWhenFailsToRenderSVG() {
        val mockInputStream = mock(InputStream::class.java)
        given(svgWrapper.getSVGFromInputStream(mockInputStream)).willThrow(RuntimeException("some exception"))

        svgImageRenderer.renderImage(mockInputStream, target, "someName")

        Mockito.verify(logger).errorLog("SVGImageRendererImpl", "Failed to parse SVG image: some exception")
    }
}
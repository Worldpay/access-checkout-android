package com.worldpay.access.checkout.sample.images

import android.app.Activity
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.worldpay.access.checkout.sample.R
import java.io.InputStream
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.Mockito.verify

class SVGImageRendererImplTest {

    private lateinit var svgImageRenderer: SVGImageRenderer
    private lateinit var activity: Activity
    private lateinit var target: ImageView
    private lateinit var svgWrapper: SVGWrapper
    private lateinit var runOnUiThreadFun: (Runnable) -> Unit

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        activity = mock(Activity::class.java)
        target = mock(ImageView::class.java)
        svgWrapper = mock(SVGWrapper::class.java)
        runOnUiThreadFun = mock(Function1::class.java as Class<Function1<Runnable, Unit>>)
        svgImageRenderer = SVGImageRendererImpl(runOnUiThreadFun, svgWrapper)
    }

    @Test
    fun shouldRenderImageIntoTargetView() {
        val inputStream = mock(InputStream::class.java)
        val svg = mock(SVG::class.java)

        given(target.measuredWidth).willReturn(1)
        given(target.measuredHeight).willReturn(1)
        given(svgWrapper.getSVGFromInputStream(inputStream)).willReturn(svg)
        given(svg.renderToPicture(1, 1)).willReturn(mock(Picture::class.java))

        svgImageRenderer.renderImage(inputStream!!, target, "someName")

        val argumentCaptor = argumentCaptor<Runnable>()
        verify(runOnUiThreadFun).invoke(argumentCaptor.capture())

        argumentCaptor.firstValue.run()

        verify(target).setImageDrawable(ArgumentMatchers.any(PictureDrawable::class.java))
        verify(target).setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        verify(target).setTag(R.integer.card_tag, "someName")
    }

    @Test
    fun shouldLogExceptionWhenFailsToRenderSVG() {
        val mockInputStream = mock(InputStream::class.java)
        given(svgWrapper.getSVGFromInputStream(mockInputStream)).willThrow(RuntimeException("some exception"))

        svgImageRenderer.renderImage(mockInputStream, target, "someName")

        verifyZeroInteractions(target)
    }
}

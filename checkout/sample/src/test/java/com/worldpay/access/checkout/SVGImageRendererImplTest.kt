package com.worldpay.access.checkout

import android.app.Activity
import android.widget.ImageView
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SVGImageRendererImplTest {

    private lateinit var svgImageRenderer: SVGImageRenderer
    private lateinit var activity: Activity
    private lateinit var target: ImageView

    @Before
    fun setup() {
        activity = Mockito.mock(Activity::class.java)
        target = Mockito.mock(ImageView::class.java)
        svgImageRenderer = SVGImageRendererImpl(activity, target)
    }

    @Test
    fun shouldRenderImageIntoTargetView() {
        val inputStream = javaClass.classLoader.getResourceAsStream("test_logo.svg")

        svgImageRenderer.renderImage(inputStream)
    }

}
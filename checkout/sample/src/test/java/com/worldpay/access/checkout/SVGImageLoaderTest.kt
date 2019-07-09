package com.worldpay.access.checkout

import android.app.Activity
import android.content.res.Resources
import android.widget.ImageView
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.given
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardBrandImage
import com.worldpay.access.checkout.views.PANLayout
import okhttp3.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File
import java.io.IOException
import java.io.InputStream

class SVGImageLoaderTest {

    private lateinit var activity: Activity
    private lateinit var targetImageView: ImageView
    private lateinit var client: OkHttpClient
    private lateinit var svgImageRenderer: SVGImageRenderer
    private lateinit var svgImageLoader: SVGImageLoader
    private lateinit var cacheDir: File
    private lateinit var uiRunner: (Runnable) -> Unit

    @Before
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        activity = mock(Activity::class.java)
        targetImageView = mock(ImageView::class.java)
        client = mock(OkHttpClient::class.java)
        svgImageRenderer = mock(SVGImageRenderer::class.java)
        cacheDir = mock(File::class.java)
        uiRunner = mock(Function1::class.java as Class<Function1<Runnable, Unit>>)
        svgImageLoader = SVGImageLoader(uiRunner, cacheDir, client, svgImageRenderer)
    }

    @Test
    fun shouldFetchIdentifiedBrandLogoAndSetOnTargetView() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/svg+xml", "http://localhost/test.svg")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrand, targetImageView)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onResponse callback function
        captor.firstValue.onResponse(mockHttpCall, response)
        verify(svgImageRenderer).renderImage(inputStream, targetImageView, "test")
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        val resources = mock(Resources::class.java)
        given(resources.getResourceEntryName(R.drawable.card_unknown_logo)).willReturn("card_unknown_logo")
        given(targetImageView.resources).willReturn(resources)

        svgImageLoader.fetchAndApplyCardLogo(null, targetImageView)

        verifyZeroInteractions(client)

        verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
        verify(targetImageView).setTag(PANLayout.CARD_TAG, "card_unknown_logo")
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoIfNoSvgLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/png", "http://localhost/test.png")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val resources = mock(Resources::class.java)
        given(resources.getResourceEntryName(R.drawable.card_unknown_logo)).willReturn("card_unknown_logo")
        given(targetImageView.resources).willReturn(resources)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrand, targetImageView)

        verifyZeroInteractions(client)
        verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
        verify(targetImageView).setTag(PANLayout.CARD_TAG, "card_unknown_logo")
    }

    @Test
    fun shouldNotUpdateCardBrandLogoOnFailureToFetchLogo() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/svg+xml", "http://localhost/test.svg")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrand, targetImageView)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onFailure callback function
        captor.firstValue.onFailure(mockHttpCall, IOException("some message"))
        verifyZeroInteractions(svgImageRenderer)
        verifyZeroInteractions(targetImageView)
    }


}
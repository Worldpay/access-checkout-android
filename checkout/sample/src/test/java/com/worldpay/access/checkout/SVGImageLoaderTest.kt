package com.worldpay.access.checkout

import android.app.Activity
import android.widget.ImageView
import com.worldpay.access.checkout.model.CardBrand
import com.worldpay.access.checkout.model.CardBrandImage
import okhttp3.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.io.IOException
import java.io.InputStream

class SVGImageLoaderTest {

    private lateinit var activity: Activity
    private lateinit var targetImageView: ImageView
    private lateinit var client: OkHttpClient
    private lateinit var svgImageRenderer: SVGImageRenderer

    @Before
    fun setup() {
        activity = mock(Activity::class.java)
        targetImageView = mock(ImageView::class.java)
        client = mock(OkHttpClient::class.java)
        svgImageRenderer = mock(SVGImageRenderer::class.java)
    }

    @Test
    fun shouldFetchIdentifiedBrandLogoAndSetOnTargetView() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/svg+xml", "http://localhost/test.svg")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(typeSafeAny())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        SVGImageLoader.fetchAndApplyCardLogo(activity, cardBrand, targetImageView, client, svgImageRenderer)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        Mockito.verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onResponse callback function
        captor.value.onResponse(mockHttpCall, response)
        Mockito.verify(svgImageRenderer).renderImage(inputStream)
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        SVGImageLoader.fetchAndApplyCardLogo(activity, null, targetImageView, client, svgImageRenderer)

        Mockito.verifyZeroInteractions(client)
        Mockito.verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoIfNoSvgLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/png", "http://localhost/test.png")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(typeSafeAny())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        SVGImageLoader.fetchAndApplyCardLogo(activity, cardBrand, targetImageView, client, svgImageRenderer)

        Mockito.verifyZeroInteractions(client)
        Mockito.verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
    }

    @Test
    fun shouldNotUpdateCardBrandLogoOnFailureToFetchLogo() {
        val cardBrand = CardBrand("test", listOf(CardBrandImage("image/svg+xml", "http://localhost/test.svg")), null, emptyList())
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(typeSafeAny())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body()).willReturn(responseBody)

        SVGImageLoader.fetchAndApplyCardLogo(activity, cardBrand, targetImageView, client, svgImageRenderer)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        Mockito.verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onFailure callback function
        captor.value.onFailure(mockHttpCall, IOException("some message"))
        Mockito.verifyZeroInteractions(svgImageRenderer)
        Mockito.verifyZeroInteractions(targetImageView)
    }

    private fun <T> typeSafeAny(): T {
        Mockito.any<T>()
        return null as T
    }

    private inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)

}
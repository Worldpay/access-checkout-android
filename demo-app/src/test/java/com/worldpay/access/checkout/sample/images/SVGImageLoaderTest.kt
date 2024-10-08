package com.worldpay.access.checkout.sample.images

import android.app.Activity
import android.content.res.Resources
import android.widget.ImageView
import com.worldpay.access.checkout.client.validation.model.CardBrand
import com.worldpay.access.checkout.client.validation.model.CardBrandImage
import com.worldpay.access.checkout.sample.R
import java.io.File
import java.io.IOException
import java.io.InputStream
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.given
import org.mockito.kotlin.verifyNoInteractions

class SVGImageLoaderTest {

    private val cardBrand =
        CardBrand(
            name = "visa",
            images = listOf(
                CardBrandImage(
                    type = "image/svg+xml",
                    url = "https://localhost:8443/test.svg"
                )
            )
        )

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
        svgImageLoader =
            SVGImageLoader(uiRunner, cacheDir, client, svgImageRenderer)
    }

    @Test
    fun shouldFetchIdentifiedBrandLogoAndSetOnTargetView() {

        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrand, targetImageView)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onResponse callback function
        captor.firstValue.onResponse(mockHttpCall, response)
        verify(svgImageRenderer).renderImage(inputStream, targetImageView, "visa")
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        val resources = mock(Resources::class.java)
        given(resources.getResourceEntryName(R.drawable.card_unknown_logo)).willReturn("card_unknown_logo")
        given(targetImageView.resources).willReturn(resources)

        svgImageLoader.fetchAndApplyCardLogo(null, targetImageView)

        verifyNoInteractions(client)

        verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
        verify(targetImageView).setTag(R.integer.card_tag, "card_unknown_logo")
    }

    @Test
    fun shouldNotAttemptToFetchRemoteCardLogoIfNoSvgLogoForUnidentifiedBrandAndShouldSetUnknownCardLogo() {
        val cardBrandWithNoSVG =
            CardBrand(
                name = "visa",
                images = listOf(
                    CardBrandImage(
                        type = "image/png",
                        url = "https://localhost:8443/test.png"
                    )
                )
            )

        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val resources = mock(Resources::class.java)
        given(resources.getResourceEntryName(R.drawable.card_unknown_logo)).willReturn("card_unknown_logo")
        given(targetImageView.resources).willReturn(resources)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrandWithNoSVG, targetImageView)

        verifyNoInteractions(client)
        verify(targetImageView).setImageResource(R.drawable.card_unknown_logo)
        verify(targetImageView).setTag(R.integer.card_tag, "card_unknown_logo")
    }

    @Test
    fun shouldNotUpdateCardBrandLogoOnFailureToFetchLogo() {
        val mockHttpCall = mock(Call::class.java)
        given(client.newCall(any())).willReturn(mockHttpCall)

        val response = mock(Response::class.java)
        val responseBody = mock(ResponseBody::class.java)
        val inputStream = mock(InputStream::class.java)
        given(responseBody.byteStream()).willReturn(inputStream)
        given(response.body).willReturn(responseBody)

        svgImageLoader.fetchAndApplyCardLogo(cardBrand, targetImageView)

        val captor = argumentCaptor<Callback>()
        // Verify http request was made
        verify(mockHttpCall).enqueue(captor.capture())

        // Trigger onFailure callback function
        captor.firstValue.onFailure(mockHttpCall, IOException("some message"))
        verifyNoInteractions(svgImageRenderer)
        verifyNoInteractions(targetImageView)
    }
}

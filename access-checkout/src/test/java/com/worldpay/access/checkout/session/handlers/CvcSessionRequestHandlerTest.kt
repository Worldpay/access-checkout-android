package com.worldpay.access.checkout.session.handlers

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType.CARD
import com.worldpay.access.checkout.client.session.model.SessionType.CVC
import com.worldpay.access.checkout.session.api.SessionRequestService.Companion.REQUEST_KEY
import com.worldpay.access.checkout.session.api.request.CvcSessionRequest
import com.worldpay.access.checkout.session.api.request.SessionRequestInfo
import com.worldpay.access.checkout.testutils.PlainRobolectricTestRunner
import com.worldpay.access.checkout.ui.AccessEditText
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@RunWith(PlainRobolectricTestRunner::class)
class CvcSessionRequestHandlerTest {

    private val context = mock(Context::class.java)
    private val externalSessionResponseListener = mock(SessionResponseListener::class.java)

    private lateinit var cvcSessionRequestHandler: CvcSessionRequestHandler

    private val baseUrl = URL("http://base-url.com")

    @Before
    fun setup() {
        cvcSessionRequestHandler =
            CvcSessionRequestHandler(
                SessionRequestHandlerConfig.Builder()
                    .baseUrl(baseUrl)
                    .merchantId("merchant-id")
                    .context(context)
                    .externalSessionResponseListener(externalSessionResponseListener)
                    .build()
            )
    }

    @Test
    fun `should be able to handle a session token request`() {
        assertTrue { cvcSessionRequestHandler.canHandle(listOf(CVC)) }
    }

    @Test
    fun `should not be able to handle a verified token request`() {
        assertFalse { cvcSessionRequestHandler.canHandle(listOf(CARD)) }
    }

    @Test
    fun `should not throw illegal argument exception if pan is not provided in card details`() {
        val expiryDate = org.mockito.kotlin.mock<AccessEditText>()
        whenever(expiryDate.text).thenReturn("1120")
        val cvc = org.mockito.kotlin.mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .expiryDate(expiryDate)
            .cvc(cvc)
            .build()
        cvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should not throw illegal argument exception if expiry date is not provided in card details`() {
        val pan = mock<AccessEditText>()
        whenever(pan.text).thenReturn("123456789")
        val cvc = org.mockito.kotlin.mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .pan(pan)
            .cvc(cvc)
            .build()
        cvcSessionRequestHandler.handle(cardDetails)
    }

    @Test
    fun `should throw illegal argument exception if cvc is not provided in card details`() {
        val cardDetails = CardDetails.Builder().build()

        val exception = assertFailsWith<IllegalArgumentException> {
            cvcSessionRequestHandler.handle(cardDetails)
        }

        assertEquals("Expected cvc to be provided but was not", exception.message)
    }

    @Test
    fun `should start service via context using the expected intent`() {
        val cvc = org.mockito.kotlin.mock<AccessEditText>()
        whenever(cvc.text).thenReturn("123")

        val cardDetails = CardDetails.Builder()
            .cvc(cvc)
            .build()

        cvcSessionRequestHandler.handle(cardDetails)

        val argument = ArgumentCaptor.forClass(Intent::class.java)

        verify(context).startService(argument.capture())

        val sessionRequestInfo = argument.value.getSerializableExtra(REQUEST_KEY) as SessionRequestInfo

        sessionRequestInfo.requestBody as CvcSessionRequest

        assertEquals("merchant-id", sessionRequestInfo.requestBody.identity)
        assertEquals(cardDetails.cvc, sessionRequestInfo.requestBody.cvc)

        assertEquals(baseUrl, sessionRequestInfo.baseUrl)

        assertEquals(DiscoverLinks.sessions, sessionRequestInfo.discoverLinks)
        assertEquals(CVC, sessionRequestInfo.sessionType)

        assertEquals(1, argument.value.extras?.size())
    }
}

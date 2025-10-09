package com.worldpay.access.checkout.client.testutil

import android.content.Context
import android.os.Looper.getMainLooper
import android.text.method.DigitsKeyListener
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_DEL
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.test.core.app.ApplicationProvider
import com.github.tomakehurst.wiremock.WireMockServer
import com.worldpay.access.checkout.BaseCoroutineTest
import com.worldpay.access.checkout.client.AccessCheckoutClient
import com.worldpay.access.checkout.client.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.testutil.mocks.AccessWPServiceMock
import com.worldpay.access.checkout.client.testutil.mocks.CardBinServiceMock
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.ui.AccessCheckoutEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import okhttp3.mockwebserver.MockWebServer
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.robolectric.Shadows.shadowOf
import javax.net.ssl.HttpsURLConnection
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
open class AbstractValidationIntegrationTest : BaseCoroutineTest() {

    protected lateinit var context: Context

    protected lateinit var pan: AccessCheckoutEditText
    protected lateinit var cvc: AccessCheckoutEditText
    protected lateinit var expiryDate: AccessCheckoutEditText

    private val lifecycleOwner = mock<LifecycleOwner>()
    private val lifecycle = mock<Lifecycle>()

    protected lateinit var cardValidationListener: CardValidationListener

    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()

    private lateinit var accessMockServer: MockWebServer
    private lateinit var cardBinServer: WireMockServer

    private val sessionResponseListener = mock<SessionResponseListener>()
    protected lateinit var accessCheckoutClient: AccessCheckoutClient

    @BeforeTest
    fun baseSetUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        resetValidation()
        HttpsURLConnection.setDefaultSSLSocketFactory(TrustAllSSLSocketFactory())
        setupMockServices()
        createAccessCheckoutClient(context)
    }

    private fun setupMockServices(): Pair<MockWebServer, WireMockServer> {
        this.accessMockServer = AccessWPServiceMock.start()
        this.cardBinServer = CardBinServiceMock.start()
        return Pair(accessMockServer, cardBinServer)
    }


    @AfterTest
    fun baseTearDown() {
        tearDownMockServers()
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
    }

    fun tearDownMockServers() {
        AccessWPServiceMock.shutdown()
        CardBinServiceMock.shutdown()
    }

    protected fun initialiseValidation(
        enablePanFormatting: Boolean = false,
        acceptedCardBrands: Array<String>? = null
    ) {
        resetValidation()

        val cardValidationConfig = CardValidationConfig.Builder()
            .pan(pan)
            .cvc(cvc)
            .expiryDate(expiryDate)
            .validationListener(cardValidationListener)
            .lifecycleOwner(lifecycleOwner)

        if (enablePanFormatting) {
            cardValidationConfig.enablePanFormatting()
        }

        if (acceptedCardBrands != null) {
            cardValidationConfig.acceptedCardBrands(acceptedCardBrands)
        }

        accessCheckoutClient.initialiseValidation(cardValidationConfig.build())
    }

    private fun resetValidation() {
        pan = AccessCheckoutEditText(context)
        pan.id = 1
        pan.keyListener = DigitsKeyListener.getInstance("0123456789")
        expiryDate = AccessCheckoutEditText(context)
        expiryDate.id = 2
        cvc = AccessCheckoutEditText(context)
        cvc.id = 3

        cardValidationListener = spy(CardValidationListener())
        reset(cardValidationListener)

        reset(lifecycle, lifecycleOwner)
        given(lifecycleOwner.lifecycle).willReturn(lifecycle)
    }

    protected fun AccessCheckoutEditText.pressBackspaceAtIndex(selection: Int) {
        this.editText!!.setSelection(selection)
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
    }

    protected fun AccessCheckoutEditText.pressBackspaceAtSelection(start: Int, end: Int) {
        this.editText!!.setSelection(start, end)
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, KEYCODE_DEL, 0))
        this.editText.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, KEYCODE_DEL, 0))
    }

    private fun AccessCheckoutEditText.typeAtIndex(selection: Int, text: String) {
        this.editText!!.setSelection(selection)
        this.editText.text.insert(selection, text)
//        this.dispatchKeyEvent(KeyEvent(0, 0, ACTION_DOWN, code, 0))
//        this.dispatchKeyEvent(KeyEvent(0, 0, ACTION_UP, code, 0))
    }

    private fun AccessCheckoutEditText.paste(
        selectionStart: Int,
        selectionEnd: Int,
        text: String
    ) {
        this.editText!!.text.replace(selectionStart, selectionEnd, text)
    }

    fun TestScope.typeAtIndex(
        editText: AccessCheckoutEditText,
        selection: Int,
        text: String
    ) {
        performSafeUiAction {
            editText.typeAtIndex(selection, text)
        }
    }

    fun TestScope.paste(
        editText: AccessCheckoutEditText, selectionStart: Int,
        selectionEnd: Int,
        text: String
    ) {
        performSafeUiAction {
            editText.paste(selectionStart, selectionEnd, text)
        }
    }

    fun TestScope.setText(editText: AccessCheckoutEditText, text: String) {
        performSafeUiAction {
            editText.setText(text)
        }
    }

    private fun TestScope.performSafeUiAction(
        editAction: () -> Unit
    ) {
        testScheduler.advanceUntilIdle() //Complete any pending co-routines
        editAction()
        testScheduler.advanceUntilIdle() //Complete edit action co-routines
        shadowOf(getMainLooper()).idle() //Execute all tasks in main looper
    }

    private fun createAccessCheckoutClient(context: Context) {
        val baseUrl = accessMockServer.url("/").toString()

        accessCheckoutClient = AccessCheckoutClientBuilder()
            .baseUrl(baseUrl)
            .checkoutId("INTEGRATION-TEST")
            .sessionResponseListener(sessionResponseListener)
            .context(context)
            .lifecycleOwner(lifecycleOwner)
            .build()
    }
}

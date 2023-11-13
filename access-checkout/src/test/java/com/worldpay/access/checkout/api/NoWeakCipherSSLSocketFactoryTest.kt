package com.worldpay.access.checkout.api

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

class NoWeakCipherSSLSocketFactoryTest {
    private val TEST_CIPHER_SUITES = arrayOf(
        "TLS_RSA_WITH_AES_256_CBC_SHA",
        "TLS_CHACHA20_POLY1305_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_DHE_PSK_WITH_AES_256_CCM",
        "TLS_RSA_WITH_3DES_EDE_CBC_Sha"
    )

    @Mock
    private val mockDefaultSslSocketFactory = mock<SSLSocketFactory>()

    @Test
    fun `noWeakCipherSSLSocketFactory() returns unique instance of ssl socket factory`() {
        val instance1: SSLSocketFactory =
            NoWeakCipherSSLSocketFactory.noWeakCipherSSLSocketFactory()
        val instance2: SSLSocketFactory =
            NoWeakCipherSSLSocketFactory.noWeakCipherSSLSocketFactory()

        assertTrue(instance1 is NoWeakCipherSSLSocketFactory)
        assertTrue(instance2 is NoWeakCipherSSLSocketFactory)
        assertEquals(instance1, instance2)
    }

    @Test
    fun `createSocket() overload 1 delegates call to default ssl socket factory`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        val socket = mock<Socket>()
        val host = "some host"
        val port = 1234
        val autoClose = true

        val expectedSocket = mock<SSLSocket>()
        given(mockDefaultSslSocketFactory.createSocket(socket, host, port, autoClose)).willReturn(
            expectedSocket
        )

        val result = noWeakCipherSSLSocketFactory.createSocket(socket, host, port, autoClose)

        assertEquals(expectedSocket, result)
    }

    @Test
    fun `createSocket() overload 2 delegates call to default ssl socket factory`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        val host = "some host"
        val port = 1234

        val expectedSocket = mock<SSLSocket>()
        given(mockDefaultSslSocketFactory.createSocket(host, port)).willReturn(
            expectedSocket
        )

        val result = noWeakCipherSSLSocketFactory.createSocket(host, port)

        assertEquals(expectedSocket, result)
    }

    @Test
    fun `createSocket() overload 3 delegates call to default ssl socket factory`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        val host = "some host"
        val port = 1234
        val localHost = mock<InetAddress>()
        val localPort = 5678

        val expectedSocket = mock<SSLSocket>()
        given(
            mockDefaultSslSocketFactory.createSocket(
                host,
                port,
                localHost,
                localPort
            )
        ).willReturn(
            expectedSocket
        )

        val result = noWeakCipherSSLSocketFactory.createSocket(host, port, localHost, localPort)

        assertEquals(expectedSocket, result)
    }

    @Test
    fun `createSocket() overload 4 delegates call to default ssl socket factory`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        val host = mock<InetAddress>()
        val port = 1234

        val expectedSocket = mock<SSLSocket>()
        given(mockDefaultSslSocketFactory.createSocket(host, port)).willReturn(
            expectedSocket
        )

        val result = noWeakCipherSSLSocketFactory.createSocket(host, port)

        assertEquals(expectedSocket, result)
    }

    @Test
    fun `createSocket() overload 5 delegates call to default ssl socket factory`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        val address = mock<InetAddress>()
        val port = 1234
        val localAddress = mock<InetAddress>()
        val localPort = 5678

        val expectedSocket = mock<SSLSocket>()
        given(
            mockDefaultSslSocketFactory.createSocket(
                address,
                port,
                localAddress,
                localPort
            )
        ).willReturn(
            expectedSocket
        )

        val result =
            noWeakCipherSSLSocketFactory.createSocket(address, port, localAddress, localPort)

        assertEquals(expectedSocket, result)
    }

    @Test
    fun `defaultCipherSuites() delegates call to default ssl factory and removes SHA1 cipher suites independently of case`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        given(mockDefaultSslSocketFactory.defaultCipherSuites).willReturn(TEST_CIPHER_SUITES)

        val result = noWeakCipherSSLSocketFactory.defaultCipherSuites

        val expectedCipherSuites = arrayOf(
            "TLS_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_PSK_WITH_AES_256_CCM"
        )
        assertEquals(expectedCipherSuites.toList(), result.toList())
        verify(mockDefaultSslSocketFactory).defaultCipherSuites
    }

    @Test
    fun `supportedCipherSuites delegates call to default ssl factory and removes SHA1 cipher suites independently of case`() {
        val noWeakCipherSSLSocketFactory = createNoWeakCipherSSLSocketFactory()
        given(mockDefaultSslSocketFactory.supportedCipherSuites).willReturn(TEST_CIPHER_SUITES)

        val result = noWeakCipherSSLSocketFactory.supportedCipherSuites

        val expectedCipherSuites = arrayOf(
            "TLS_CHACHA20_POLY1305_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_PSK_WITH_AES_256_CCM"
        )
        assertEquals(expectedCipherSuites.toList(), result.toList())
    }

    private fun createNoWeakCipherSSLSocketFactory(): NoWeakCipherSSLSocketFactory {
        given(mockDefaultSslSocketFactory.defaultCipherSuites).willReturn(TEST_CIPHER_SUITES)
        given(mockDefaultSslSocketFactory.supportedCipherSuites).willReturn(TEST_CIPHER_SUITES)

        return NoWeakCipherSSLSocketFactory(mockDefaultSslSocketFactory)
    }
}

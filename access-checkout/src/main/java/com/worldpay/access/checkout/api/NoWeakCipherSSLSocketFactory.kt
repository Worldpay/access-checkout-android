package com.worldpay.access.checkout.api

import java.net.InetAddress
import java.net.Socket
import java.util.Arrays
import java.util.stream.Collectors.toList
import javax.net.ssl.HttpsURLConnection.getDefaultSSLSocketFactory
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

internal class NoWeakCipherSSLSocketFactory constructor(
    private val defaultSSLSocketFactory: SSLSocketFactory = getDefaultSSLSocketFactory()
) : SSLSocketFactory() {

    private val enabledCipherSuites: Array<String> = Arrays
        .stream(defaultSSLSocketFactory.defaultCipherSuites)
        .filter { !it.endsWith("SHA", ignoreCase = true) }
        .collect(toList())
        .toTypedArray()

    companion object {
        private val noWeakCipherSSLSocketFactory =
            NoWeakCipherSSLSocketFactory(getDefaultSSLSocketFactory())

        internal fun noWeakCipherSSLSocketFactory(): SSLSocketFactory {
            return noWeakCipherSSLSocketFactory
        }
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        val socket = defaultSSLSocketFactory.createSocket(s, host, port, autoClose)
        (socket as SSLSocket).enabledCipherSuites = enabledCipherSuites
        return socket
    }

    override fun createSocket(host: String?, port: Int): Socket {
        val socket = defaultSSLSocketFactory.createSocket(host, port)
        (socket as SSLSocket).enabledCipherSuites = enabledCipherSuites
        return socket
    }

    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket {
        val socket = defaultSSLSocketFactory.createSocket(host, port, localHost, localPort)
        (socket as SSLSocket).enabledCipherSuites = enabledCipherSuites
        return socket
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        val socket = defaultSSLSocketFactory.createSocket(host, port)
        (socket as SSLSocket).enabledCipherSuites = enabledCipherSuites
        return socket
    }

    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket {
        val socket =
            defaultSSLSocketFactory.createSocket(address, port, localAddress, localPort)
        (socket as SSLSocket).enabledCipherSuites = enabledCipherSuites
        return socket
    }

    /**
     * This method is unused as it's the Socket's enabledCiperSuites that matter
     * but it is overridden for consistency
     */
    override fun getDefaultCipherSuites(): Array<String> {
        return enabledCipherSuites
    }

    /**
     * This method is unused but is overridden for consistency
     */
    override fun getSupportedCipherSuites(): Array<String> {
        return enabledCipherSuites
    }
}

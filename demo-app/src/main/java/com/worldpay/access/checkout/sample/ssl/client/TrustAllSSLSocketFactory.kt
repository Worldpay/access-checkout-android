package com.worldpay.access.checkout.sample.ssl.client

import android.annotation.SuppressLint
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class TrustAllSSLSocketFactory : SSLSocketFactory() {

    private val sslContext = SSLContext.getInstance("TLS")

    companion object {
        val X509_TRUST_MANAGER = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(xcs: Array<X509Certificate?>?, string: String?) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(xcs: Array<X509Certificate?>?, string: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }
    }

    init {
        sslContext.init(null, arrayOf(X509_TRUST_MANAGER), SecureRandom())
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return emptyArray()
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(socket: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? {
        return sslContext.socketFactory.createSocket(socket, host, port, autoClose)
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket? {
        return sslContext.socketFactory.createSocket()
    }

    override fun createSocket(host: String?, port: Int): Socket {
        return sslContext.socketFactory.createSocket(host, port)
    }

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        return sslContext.socketFactory.createSocket(host, port, localHost, localPort)
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return sslContext.socketFactory.createSocket(host, port)
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        return sslContext.socketFactory.createSocket(address, port, localAddress, localPort)
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return emptyArray()
    }
}

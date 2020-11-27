package com.worldpay.access.checkout.sample.ssl

import org.eclipse.jetty.util.ssl.SslContextFactory
import javax.net.ssl.SSLEngine

class NoSslFactory: SslContextFactory() {

    override fun customize(sslEngine: SSLEngine?) {}
}

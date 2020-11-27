package com.worldpay.access.checkout.sample.ssl

import com.github.tomakehurst.wiremock.core.Options
import com.github.tomakehurst.wiremock.http.AdminRequestHandler
import com.github.tomakehurst.wiremock.http.StubRequestHandler
import com.github.tomakehurst.wiremock.jetty9.JettyHttpServer
import com.github.tomakehurst.wiremock.jetty92.Jetty92MultipartRequestConfigurer
import com.github.tomakehurst.wiremock.servlet.MultipartRequestConfigurer
import org.eclipse.jetty.util.ssl.SslContextFactory


class CustomHttpServer(options: Options?,
                       adminRequestHandler: AdminRequestHandler?,
                       stubRequestHandler: StubRequestHandler?
) : JettyHttpServer(options, adminRequestHandler, stubRequestHandler) {

    override fun buildSslContextFactory(): SslContextFactory {
        return NoSslFactory()
    }

    override fun buildMultipartRequestConfigurer(): MultipartRequestConfigurer {
        return Jetty92MultipartRequestConfigurer()
    }
}

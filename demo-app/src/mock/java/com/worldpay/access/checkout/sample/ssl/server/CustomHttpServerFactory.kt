package com.worldpay.access.checkout.sample.ssl.server

import com.github.tomakehurst.wiremock.core.Options
import com.github.tomakehurst.wiremock.http.AdminRequestHandler
import com.github.tomakehurst.wiremock.http.HttpServer
import com.github.tomakehurst.wiremock.http.HttpServerFactory
import com.github.tomakehurst.wiremock.http.StubRequestHandler

class CustomHttpServerFactory : HttpServerFactory {

    override fun buildHttpServer(
        options: Options,
        adminRequestHandler: AdminRequestHandler,
        stubRequestHandler: StubRequestHandler
    ): HttpServer {
        return CustomHttpServer(
            options,
            adminRequestHandler,
            stubRequestHandler
        )
    }


}

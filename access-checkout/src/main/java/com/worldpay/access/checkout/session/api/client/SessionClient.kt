package com.worldpay.access.checkout.session.api.client

import com.worldpay.access.checkout.session.api.request.SessionRequest
import com.worldpay.access.checkout.session.api.response.SessionResponse
import java.net.URL

/**
 * This interface represents a contract for a client that retrieves a [SessionResponse] by making a
 * post request to Access Worldpay services.
 */
internal interface SessionClient {

    /**
     * Uses the given httpClient to make a request to the API to retrieve a session response
     *
     * @param[url] the [URL] for the service
     * @param[request] [SessionRequest] containing session request info
     *
     * @return [SessionResponse] representation of response from the service
     */
    suspend fun getSessionResponse(url: URL, request: SessionRequest): SessionResponse
}

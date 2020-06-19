package com.worldpay.access.checkout.api.exception

object RequestErrorProvider {

    fun getErrorForName(errorName: String): RequestError =
        when (errorName) {
            RequestError.BODY_IS_NOT_JSON.errorName -> RequestError.BODY_IS_NOT_JSON
            RequestError.BODY_IS_EMPTY.errorName -> RequestError.BODY_IS_EMPTY
            RequestError.BODY_DOES_NOT_MATCH_SCHEMA.errorName -> RequestError.BODY_DOES_NOT_MATCH_SCHEMA
            RequestError.RESOURCE_NOT_FOUND.errorName -> RequestError.RESOURCE_NOT_FOUND
            RequestError.ENDPOINT_NOT_FOUND.errorName -> RequestError.ENDPOINT_NOT_FOUND
            RequestError.METHOD_NOT_ALLOWED.errorName -> RequestError.METHOD_NOT_ALLOWED
            RequestError.UNSUPPORTED_ACCEPT_HEADER.errorName -> RequestError.UNSUPPORTED_ACCEPT_HEADER
            RequestError.UNSUPPORTED_CONTENT_TYPE.errorName -> RequestError.UNSUPPORTED_CONTENT_TYPE
            RequestError.INTERNAL_ERROR_OCCURRED.errorName -> RequestError.INTERNAL_ERROR_OCCURRED
            else -> RequestError.UNKNOWN_ERROR
        }
    
}

enum class RequestError(val errorCode: Int, val errorName: String) {
    BODY_IS_NOT_JSON(400, "bodyIsNotJson"),
    BODY_IS_EMPTY(400, "bodyIsEmpty"),
    BODY_DOES_NOT_MATCH_SCHEMA(400, "bodyDoesNotMatchSchema"),
    RESOURCE_NOT_FOUND(404, "resourceNotFound"),
    ENDPOINT_NOT_FOUND(404, "endpointNotFound"),
    METHOD_NOT_ALLOWED(405, "methodNotAllowed"),
    UNSUPPORTED_ACCEPT_HEADER(406, "unsupportedAcceptHeader"),
    UNSUPPORTED_CONTENT_TYPE(415, "unsupportedContentType"),
    INTERNAL_ERROR_OCCURRED(500, "internalErrorOccurred"),
    UNKNOWN_ERROR(500, "unknownError")
}

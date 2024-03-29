package com.worldpay.access.checkout.session.api.response

import com.worldpay.access.checkout.session.api.response.SessionResponse.Links
import java.io.Serializable

/**
 * A deserialised response from a Session request
 *
 * @property [links] [Links] representing the discovered endpoints
 */
internal data class SessionResponse(val links: Links) : Serializable {
    internal data class Links(val endpoints: Endpoints, val curies: Array<Curies>) : Serializable {
        internal data class Endpoints(val href: String) : Serializable
        internal data class Curies(val href: String, val name: String, val templated: Boolean) : Serializable

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            val links = other !!as Links

            if (endpoints != links.endpoints) return false
            if (!curies.contentEquals(links.curies)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = endpoints.hashCode()
            result = 31 * result + curies.contentHashCode()
            return result
        }
    }
}

package com.worldpay.access.checkout.api

import java.io.Serializable

internal data class SessionResponse(val links: Links): Serializable {
    internal data class Links(val verifiedTokensSession: VerifiedTokensSession, val curies: Array<Curies>): Serializable {
        internal data class VerifiedTokensSession(val href: String): Serializable
        internal data class Curies(val href: String, val name: String, val templated: Boolean): Serializable

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Links

            if (verifiedTokensSession != other.verifiedTokensSession) return false
            if (!curies.contentEquals(other.curies)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = verifiedTokensSession.hashCode()
            result = 31 * result + curies.contentHashCode()
            return result
        }
    }
}
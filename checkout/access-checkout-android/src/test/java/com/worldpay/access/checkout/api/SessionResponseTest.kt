package com.worldpay.access.checkout.api

import org.junit.Assert.*
import org.junit.Test

class SessionResponseTest {

    @Test
    fun shouldConstructSessionResponse() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertEquals(sessionResponse.links.verifiedTokensSession, SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"))
        assertArrayEquals(sessionResponse.links.curies, arrayOf(SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true)))
        assertEquals(sessionResponse.links.curies[0].href, "https://access.worldpay.com/rels/verifiedTokens{rel}.json")
        assertEquals(sessionResponse.links.curies[0].name, "verifiedTokens")
        assertEquals(sessionResponse.links.curies[0].templated, true)
    }

    @Test
    fun givenSameSessionResponseContent_ThenShouldBeEqual() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        val sessionResponse2 = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertTrue(sessionResponse == sessionResponse2)
    }

    @Test
    fun givenDifferentTokensSessionResponseContent_ThenShouldNotBeEqual() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://try.access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        val sessionResponse2 = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertTrue(sessionResponse != sessionResponse2)
    }

    @Test
    fun givenDifferentCuriesSessionResponseContent_ThenShouldNotBeEqual() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        val sessionResponse2 = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://try.access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertTrue(sessionResponse != sessionResponse2)
    }

    @Test
    fun givenSameSessionResponseObject_ThenShouldBeEqual() {
        val sessionResponse = SessionResponse(
            SessionResponse.Links(
                SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
                arrayOf(
                    SessionResponse.Links.Curies(
                        "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                        "verifiedTokens",
                        true
                    )
                )
            )
        )
        assertTrue(sessionResponse == sessionResponse)
    }

    @Test
    fun givenDifferentClasses_ThenShouldNotBeEqual() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))


        assertTrue(!sessionResponse.equals(Object()))
    }

    @Test
    fun givenSameLinksObject_ThenShouldBeEqual() {
        val links = SessionResponse.Links(
                SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
                arrayOf(
                    SessionResponse.Links.Curies(
                        "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                        "verifiedTokens",
                        true
                    )
                )
            )
        assertTrue(links == links)
    }

    @Test
    fun givenDifferentLinksObject_ThenShouldNotBeEqual() {
        val links = SessionResponse.Links(
            SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
            arrayOf(
                SessionResponse.Links.Curies(
                    "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                    "verifiedTokens",
                    true
                )
            )
        )
        assertTrue(!links.equals(Object()))
    }

    @Test
    fun givenNullLinksObject_ThenShouldNotBeEqual() {
        val links = SessionResponse.Links(
            SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"),
            arrayOf(
                SessionResponse.Links.Curies(
                    "https://access.worldpay.com/rels/verifiedTokens{rel}.json",
                    "verifiedTokens",
                    true
                )
            )
        )
        assertTrue(!links.equals(null))
    }

    @Test
    fun givenSameSessionResponseContent_ThenShouldHaveSameHashCode() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        val sessionResponse2 = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertEquals(sessionResponse.hashCode(), sessionResponse2.hashCode())
    }

    @Test
    fun givenDifferentSessionResponseContent_ThenShouldNotHaveSameHashCode() {
        val sessionResponse = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://try.access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://try.access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        val sessionResponse2 = SessionResponse(SessionResponse.Links(SessionResponse.Links.VerifiedTokensSession("http://access.worldpay.com/verifiedTokens/sessions/<encrypted-data>"), arrayOf(
            SessionResponse.Links.Curies("https://access.worldpay.com/rels/verifiedTokens{rel}.json", "verifiedTokens", true))))

        assertNotEquals(sessionResponse.hashCode(), sessionResponse2.hashCode())
    }
}
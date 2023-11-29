package com.worldpay.access.checkout.session.api.response

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionResponseTest {

    @Test
    fun shouldConstructSessionResponse() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertEquals(sessionResponse.links.endpoints, SessionResponse.Links.Endpoints("https://access.worldpay.com/sessions/<encrypted-data>"))
        assertArrayEquals(sessionResponse.links.curies, arrayOf(SessionResponse.Links.Curies("https://access.worldpay.com/rels/sessions{rel}.json", "sessions", true)))
        assertEquals(sessionResponse.links.curies[0].href, "https://access.worldpay.com/rels/sessions{rel}.json")
        assertEquals(sessionResponse.links.curies[0].name, "sessions")
        assertEquals(sessionResponse.links.curies[0].templated, true)
    }

    @Test
    fun givenSameSessionResponseContent_ThenShouldBeEqual() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val sessionResponse2 =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertTrue(sessionResponse == sessionResponse2)
    }

    @Test
    fun givenDifferentTokensSessionResponseContent_ThenShouldNotBeEqual() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://npe.access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val sessionResponse2 =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertTrue(sessionResponse != sessionResponse2)
    }

    @Test
    fun givenDifferentCuriesSessionResponseContent_ThenShouldNotBeEqual() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val sessionResponse2 =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://npe.access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertTrue(sessionResponse != sessionResponse2)
    }

    @Test
    fun givenSameSessionResponseObject_ThenShouldBeEqual() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )
        assertTrue(sessionResponse == sessionResponse)
    }

    @Test
    fun givenDifferentClasses_ThenShouldNotBeEqual() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertTrue(!sessionResponse.equals(Object()))
    }

    @Test
    fun givenSameLinksObject_ThenShouldBeEqual() {
        val links = SessionResponse.Links(
            SessionResponse.Links.Endpoints("https://access.worldpay.com/sessions/<encrypted-data>"),
            arrayOf(
                SessionResponse.Links.Curies(
                    "https://access.worldpay.com/rels/sessions{rel}.json",
                    "sessions",
                    true
                )
            )
        )
        assertTrue(links == links)
    }

    @Test
    fun givenDifferentLinksObject_ThenShouldNotBeEqual() {
        val links = SessionResponse.Links(
            SessionResponse.Links.Endpoints("https://access.worldpay.com/sessions/<encrypted-data>"),
            arrayOf(
                SessionResponse.Links.Curies(
                    "https://access.worldpay.com/rels/sessions{rel}.json",
                    "sessions",
                    true
                )
            )
        )

        assertTrue(!links.equals(Object()))
    }

    @Test
    fun givenNullLinksObject_ThenShouldNotBeEqual() {
        val links = SessionResponse.Links(
            SessionResponse.Links.Endpoints("https://access.worldpay.com/sessions/<encrypted-data>"),
            arrayOf(
                SessionResponse.Links.Curies(
                    "https://access.worldpay.com/rels/sessions{rel}.json",
                    "sessions",
                    true
                )
            )
        )

        assertTrue(!links.equals(null))
    }

    @Test
    fun givenSameSessionResponseContent_ThenShouldHaveSameHashCode() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val sessionResponse2 =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertEquals(sessionResponse.hashCode(), sessionResponse2.hashCode())
    }

    @Test
    fun givenDifferentSessionResponseContent_ThenShouldNotHaveSameHashCode() {
        val sessionResponse =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://npe.access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://npe.access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        val sessionResponse2 =
            SessionResponse(
                SessionResponse.Links(
                    SessionResponse.Links.Endpoints(
                        "https://access.worldpay.com/sessions/<encrypted-data>"
                    ),
                    arrayOf(
                        SessionResponse.Links.Curies(
                            "https://access.worldpay.com/rels/sessions{rel}.json",
                            "sessions",
                            true
                        )
                    )
                )
            )

        assertNotEquals(sessionResponse.hashCode(), sessionResponse2.hashCode())
    }
}

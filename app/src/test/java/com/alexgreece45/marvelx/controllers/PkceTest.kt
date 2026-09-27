package com.alexgreece45.marvelx.controllers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PkceTest {

    @Test
    fun `GIVEN the RFC 7636 Appendix B verifier WHEN createCodeChallenge THEN returns the RFC's S256 challenge`() {
        val challenge = Pkce.createCodeChallenge("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")

        assertEquals("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", challenge)
    }

    @Test
    fun `WHEN createRandomUrlSafeToken THEN returns 86 unpadded base64url characters`() {
        val token = Pkce.createRandomUrlSafeToken()

        assertEquals(86, token.length)
        assertTrue(token.matches(Regex("^[A-Za-z0-9_-]+$")))
    }

    @Test
    fun `WHEN createRandomUrlSafeToken is called twice THEN returns different tokens`() {
        assertNotEquals(Pkce.createRandomUrlSafeToken(), Pkce.createRandomUrlSafeToken())
    }

    @Test
    fun `GIVEN a generated verifier WHEN createCodeChallenge THEN returns 43 unpadded base64url characters`() {
        val challenge = Pkce.createCodeChallenge(Pkce.createRandomUrlSafeToken())

        assertEquals(43, challenge.length)
        assertTrue(challenge.matches(Regex("^[A-Za-z0-9_-]+$")))
    }
}

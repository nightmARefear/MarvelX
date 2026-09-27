package com.alexgreece45.marvelx.core.network.interceptors

import org.junit.Assert.assertEquals
import org.junit.Test

class SensitiveDataRedactionTest {

    @Test
    fun `GIVEN the token exchange form body WHEN redactSensitiveData THEN masks the verifier and code only`() {
        val body = "client_id=mobile&code_verifier=abc-123_XYZ&code=the-code" +
            "&redirect_uri=https%3A%2F%2Fbackend.example.com%2Fdeeplink%2Foauth&grant_type=authorization_code"

        assertEquals(
            "client_id=mobile&code_verifier=██&code=██" +
                "&redirect_uri=https%3A%2F%2Fbackend.example.com%2Fdeeplink%2Foauth&grant_type=authorization_code",
            body.redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN the refresh form body WHEN redactSensitiveData THEN masks the refresh token`() {
        assertEquals(
            "client_id=mobile&refresh_token=██&grant_type=refresh_token",
            "client_id=mobile&refresh_token=eyJhbGciOi.xyz&grant_type=refresh_token".redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN a token JSON response WHEN redactSensitiveData THEN masks both tokens and keeps the rest`() {
        val body = """{"access_token":"eyJ.a.b","token_type":"Bearer","refresh_token": "eyJ.c.d","expires_in":900}"""

        assertEquals(
            """{"access_token":"██","token_type":"Bearer","refresh_token":"██","expires_in":900}""",
            body.redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN the post_login request WHEN redactSensitiveData THEN masks the access token`() {
        assertEquals(
            """{"accessToken":"██"}""",
            """{"accessToken":"Bearer eyJhbGciOiJIUzI1NiJ9.e30.sig"}""".redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN the post_login response WHEN redactSensitiveData THEN masks the signing key and keeps the algorithm`() {
        assertEquals(
            """{"sign":{"key":"██","algorithm":"HmacSHA256"}}""",
            """{"sign":{"key":"MIIC-signing-key","algorithm":"HmacSHA256"}}""".redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN credential and card JSON bodies WHEN redactSensitiveData THEN masks every secret`() {
        val body = """{"tempAuthCode":"t-1","token":"jwt","otp":"123456","newPassword":"p","password":"q",""" +
            """"cardNumber":"4242424242424242","cvv":"123","fcmToken":"f-1","userId":"42"}"""

        assertEquals(
            """{"tempAuthCode":"██","token":"██","otp":"██","newPassword":"██","password":"██",""" +
                """"cardNumber":"██","cvv":"██","fcmToken":"██","userId":"42"}""",
            body.redactSensitiveData()
        )
    }

    @Test
    fun `GIVEN a listing enum JSON with a code WHEN redactSensitiveData THEN leaves the code visible`() {
        val body = """[{"code":"COMICS","label":"Comics"},{"code":"FIGURES","label":"Figures"}]"""

        assertEquals(body, body.redactSensitiveData())
    }

    @Test
    fun `GIVEN keys that only contain a sensitive name WHEN redactSensitiveData THEN leaves them unchanged`() {
        val body = "code_challenge=E9Melhoa&code_challenge_method=S256&postcode=12345&zip_code=678"

        assertEquals(body, body.redactSensitiveData())
    }
}

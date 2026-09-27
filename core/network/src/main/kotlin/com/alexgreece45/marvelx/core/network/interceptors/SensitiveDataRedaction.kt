package com.alexgreece45.marvelx.core.network.interceptors

/**
 * Form-encoded fields (`key=value`) whose values must never reach the logs. Form bodies are only
 * used by the token endpoint, so these are its OAuth/PKCE secrets.
 */
private val sensitiveFormFields = listOf(
    "code",
    "code_verifier",
    "access_token",
    "refresh_token",
    "temp_auth_code",
    "password"
)

/**
 * JSON properties whose string values must never reach the logs. `code` is deliberately absent:
 * in JSON bodies it's a listing enum / status code, while the OAuth `code` is only ever sent
 * form-encoded.
 */
private val sensitiveJsonProperties = listOf(
    // Tokens.
    "access_token",
    "accessToken", // `post_login` request.
    "refresh_token",
    "refreshToken",
    "temp_auth_code",
    "tempAuthCode",
    "token", // Short-lived JWT for the sensitive user data.
    "fcmToken",
    // Request-signing (HMAC) key of the `post_login` response.
    "key",
    // Credentials.
    "password",
    "newPassword",
    "otp",
    // Payment card.
    "cardNumber",
    "cvv"
)

private val sensitiveFormValue =
    Regex("""(?<![\w-])(${sensitiveFormFields.joinToString("|")})=[^&\s]*""")

private val sensitiveJsonValue =
    Regex(""""(${sensitiveJsonProperties.joinToString("|")})"\s*:\s*"[^"]*"""")

private const val REDACTED = "██"

/**
 * Returns this (logged) text with the values of credentials, tokens, OAuth/PKCE secrets and payment
 * card data replaced, whether they appear as form-encoded fields or as JSON string properties.
 */
internal fun String.redactSensitiveData(): String =
    this
        .replace(sensitiveFormValue) { "${it.groupValues[1]}=$REDACTED" }
        .replace(sensitiveJsonValue) { "\"${it.groupValues[1]}\":\"$REDACTED\"" }

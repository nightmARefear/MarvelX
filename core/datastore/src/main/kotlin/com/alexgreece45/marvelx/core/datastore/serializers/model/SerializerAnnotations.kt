package com.alexgreece45.marvelx.core.datastore.serializers.model

import org.koin.core.annotation.Named

@Named
internal annotation class ConsecutiveFailedAuthenticationSerializer

@Named
internal annotation class SignatureSerializer

@Named
internal annotation class UserIdSerializer

@Named
internal annotation class AuthenticationTokensSerializer

@Named
internal annotation class PendingAuthorizationSerializer

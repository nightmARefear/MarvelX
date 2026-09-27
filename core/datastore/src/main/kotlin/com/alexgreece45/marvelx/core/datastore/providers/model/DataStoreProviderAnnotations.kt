package com.alexgreece45.marvelx.core.datastore.providers.model

import org.koin.core.annotation.Named

@Named
internal annotation class UserIdDataStore

@Named
internal annotation class AuthenticationTokensDataStore

@Named
internal annotation class SignatureDataStore

@Named
internal annotation class ConsecutiveFailedAuthenticationDataStore

@Named
internal annotation class PendingAuthorizationDataStore

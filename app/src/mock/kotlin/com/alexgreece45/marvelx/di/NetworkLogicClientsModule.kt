package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.clients.ApiClientImpl
import com.alexgreece45.marvelx.network_logic.clients.ListingClient
import com.alexgreece45.marvelx.network_logic.clients.ListingClientImpl
import com.alexgreece45.marvelx.network_logic.clients.OrderClient
import com.alexgreece45.marvelx.network_logic.clients.OrderClientImpl
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClientImpl
import org.koin.dsl.module

val networkLogicClientsModule = module {
    single<ApiClient> { ApiClientImpl(get(), get(), get()) }
    single<RefreshTokenClient> { RefreshTokenClientImpl() }
    single<ListingClient> { ListingClientImpl(get()) }
    single<OrderClient> { OrderClientImpl(get()) }
}

package com.droidcon.droidflix.data

import com.droidcon.droidflix.data.auth.AuthEvents
import com.droidcon.droidflix.data.auth.AuthInterceptor
import com.droidcon.droidflix.data.auth.TokenAuthenticator
import com.droidcon.droidflix.data.auth.TokenProvider
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val koinRetrofit = module {
    single { buildOkHttpClient(get(), get()) }

    single {
        Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .client(get())
            .addConverterFactory(xmlConverterFactory())
            .addConverterFactory(scalarConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(FlixApi::class.java) }

    single { AuthEvents() }
    single { TokenProvider() }
    single { TokenAuthenticator(get()) }
    single { AuthInterceptor(get(), get()) }

}
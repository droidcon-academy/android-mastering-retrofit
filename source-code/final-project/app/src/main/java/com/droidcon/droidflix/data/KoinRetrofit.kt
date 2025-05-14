package com.droidcon.droidflix.data

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val koinRetrofit = module {
    single { buildOkHttpClient() }

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
}
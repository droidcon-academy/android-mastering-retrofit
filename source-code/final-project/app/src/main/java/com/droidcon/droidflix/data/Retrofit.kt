package com.droidcon.droidflix.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private fun buildRetrofitKotlinx(): Retrofit {
    val json = Json { ignoreUnknownKeys = true }
    return Retrofit.Builder()
        .baseUrl(MockServerManager.baseUrl)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(xmlConverterFactory())
        .addConverterFactory(scalarConverterFactory())
        .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
        .build()
}

val api: FlixApi = buildRetrofitKotlinx().create<FlixApi>(FlixApi::class.java)
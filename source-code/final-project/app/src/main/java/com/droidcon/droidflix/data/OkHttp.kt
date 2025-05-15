package com.droidcon.droidflix.data

import com.droidcon.droidflix.data.auth.AuthInterceptor
import com.droidcon.droidflix.data.auth.TokenAuthenticator
import com.droidcon.droidflix.data.cache.CacheProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

fun buildOkHttpClient(
    authInterceptor: AuthInterceptor,
    tokenAuthenticator: TokenAuthenticator
): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(CacheProvider.getCache())
        .retryOnConnectionFailure(true)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}

fun xmlConverterFactory(): Converter.Factory = SimpleXmlStatusConverterFactory()

fun scalarConverterFactory(): Converter.Factory = ScalarsConverterFactory.create()
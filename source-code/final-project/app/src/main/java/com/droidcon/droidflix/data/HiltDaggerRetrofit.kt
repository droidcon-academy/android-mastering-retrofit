package com.droidcon.droidflix.data

import com.droidcon.droidflix.data.auth.AuthEvents
import com.droidcon.droidflix.data.auth.AuthInterceptor
import com.droidcon.droidflix.data.auth.TokenAuthenticator
import com.droidcon.droidflix.data.auth.TokenProvider
import com.droidcon.droidflix.data.model.FlixErrorResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltDaggerRetrofit {

    @Provides
    fun provideOkHttp(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = buildOkHttpClient(authInterceptor, tokenAuthenticator)

    @Provides
    @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .client(okHttp)
            .addConverterFactory(xmlConverterFactory())
            .addConverterFactory(scalarConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): FlixApi = retrofit.create(FlixApi::class.java)

    @Provides
    fun provideErrorConverter(retrofit: Retrofit): Converter<ResponseBody, FlixErrorResponse> {
        return retrofit.responseBodyConverter(FlixErrorResponse::class.java, arrayOf())
    }

    @Provides
    @Singleton
    fun tokenProvider(): TokenProvider = TokenProvider()

    @Provides
    @Singleton
    fun tokenAuthenticator(tokenProvider: TokenProvider): TokenAuthenticator = TokenAuthenticator(tokenProvider)

    @Provides
    @Singleton
    fun authInterceptor(tokenProvider: TokenProvider, authEvents: AuthEvents): AuthInterceptor = AuthInterceptor(tokenProvider, authEvents)
}
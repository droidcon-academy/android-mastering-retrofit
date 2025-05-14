package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.HiltDaggerRetrofit
import com.droidcon.droidflix.data.model.FlixErrorResponse
import com.droidcon.droidflix.data.scalarConverterFactory
import com.droidcon.droidflix.data.xmlConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HiltDaggerRetrofit::class]
)
object FakeNetworkModule {

    @Provides
    @Singleton
    fun provideFakeFlixApi(): FlixApi = FakeFlixApi()

    @Provides
    @Singleton
    fun provideFakeRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .addConverterFactory(xmlConverterFactory())
            .addConverterFactory(scalarConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideErrorConverter(retrofit: Retrofit): Converter<ResponseBody, FlixErrorResponse> {
        return retrofit.responseBodyConverter(FlixErrorResponse::class.java, emptyArray())
    }
}
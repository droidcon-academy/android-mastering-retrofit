package com.droidcon.droidflix.data.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: TokenProvider,
    private val authEvent: AuthEvents
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val accessToken = tokenProvider.getAccessToken()
        request = request.newBuilder()
            .header("Authorization", accessToken)
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            synchronized(this) {
                val newAccessToken = tokenProvider.getAccessToken()

                if (accessToken == newAccessToken) {
                    if (tokenProvider.refreshAccessToken()) {
                        request = request.newBuilder()
                            .header("Authorization", tokenProvider.getAccessToken())
                            .build()

                        response.close()
                        return chain.proceed(request)
                    } else {
                        tokenProvider.clearTokens()
                        CoroutineScope(Dispatchers.Default).launch {
                            authEvent.emitUnauthorized()
                        }
                    }
                }
            }
        }

        return response
    }
}
package com.droidcon.droidflix.data.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val tokenProvider: TokenProvider) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val successful = tokenProvider.refreshAccessToken()
        return if (successful) {
            response.request.newBuilder()
                .header("Authorization", tokenProvider.getAccessToken())
                .build()
        } else null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
package com.droidcon.droidflix.data.auth

import com.droidcon.droidflix.data.prefs.AppPreferences
import kotlin.random.Random

class TokenProvider() {

    fun getAccessToken(): String {
        return if (System.currentTimeMillis() - AppPreferences.timestamp > 120000) {
            "expired"
        } else {
            "Basic ${AppPreferences.username}:${AppPreferences.password}"
        }
    }

    fun clearTokens() {
        AppPreferences.username = ""
        AppPreferences.password = ""
        AppPreferences.timestamp = 0
    }

    // Just a simulation
    fun refreshAccessToken(): Boolean {
        Thread.sleep(500)
        return Random.nextBoolean()
    }
}
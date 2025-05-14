package com.droidcon.droidflix.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private const val PREF_NAME = "global_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_TIMESTAMP = "timestamp"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit { putString(KEY_USERNAME, value) }

    var password: String?
        get() = prefs.getString(KEY_PASSWORD, null)
        set(value) = prefs.edit { putString(KEY_PASSWORD, value) }

    var timestamp: Long
        get() = prefs.getLong(KEY_TIMESTAMP, 0L)
        set(value) = prefs.edit { putLong(KEY_TIMESTAMP, value) }
}


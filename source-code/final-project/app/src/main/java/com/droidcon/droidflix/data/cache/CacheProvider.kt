package com.droidcon.droidflix.data.cache

import android.content.Context
import okhttp3.Cache

object CacheProvider {

    private lateinit var cache: Cache

    fun init(context: Context) {
        val cacheSize = (5 * 1024 * 1024).toLong()
        cache = Cache(context.cacheDir, cacheSize)
    }

    fun getCache(): Cache {
        return cache
    }
}
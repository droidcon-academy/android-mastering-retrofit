package com.droidcon.droidflix

import android.app.Application
import com.droidcon.droidflix.data.MockServerManager
import com.droidcon.droidflix.data.cache.CacheProvider
import com.droidcon.droidflix.data.koinRetrofit
import com.droidcon.droidflix.data.prefs.AppPreferences
import com.droidcon.droidflix.ui.viewModelModule
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

@HiltAndroidApp
class FlixApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
        CacheProvider.init(this)
        if (BuildConfig.DEBUG) {
            CoroutineScope(Dispatchers.Default).launch {
                MockServerManager.start()
            }
        }
        startKoin {
            androidContext(this@FlixApplication)
            androidLogger()
            modules(koinRetrofit, viewModelModule)
        }
    }

    override fun onTerminate() {
        if (BuildConfig.DEBUG) {
            MockServerManager.shutdown()
        }
        super.onTerminate()
    }
}
package com.droidcon.droidflix.data.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class AuthEventsViewModel @Inject constructor(
    authEvents: AuthEvents
) : ViewModel() {
    val onUnauthorized: SharedFlow<Unit> = authEvents.onUnauthorized
}
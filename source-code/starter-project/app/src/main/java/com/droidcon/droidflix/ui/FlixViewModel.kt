package com.droidcon.droidflix.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidcon.droidflix.data.model.Auth
import com.droidcon.droidflix.data.model.Flix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class FlixViewModel: ViewModel() {

    private val _flixList = MutableStateFlow<List<Flix>>(emptyList())
    val flixList: StateFlow<List<Flix>> = _flixList
    private val _flix = MutableStateFlow<Flix?>(null)
    val flix: StateFlow<Flix?> = _flix
    var uiState by mutableStateOf<FlixUiState>(FlixUiState.Idle)
        private set
    val auth = mutableStateOf(Auth())
    var currentPage = 1
    private var debounceJob: Job? = null

    fun getFlix(input: String = "", page: Int = 1) {
        currentPage = page
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            delay(700)
            //TODO perform api request
            uiState = FlixUiState.Success
        }
    }

    fun updateFlix(oldflix: Flix, newFlix: Flix) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            try {
                newFlix.poster?.let {
                    if (it != oldflix.poster) uploadFlixFile(it)
                }
                newFlix.video?.let {
                    if (it != oldflix.video) uploadFlixFile(it)
                }
                //TODO perform api request
                uiState = FlixUiState.Success
            } catch (e: Exception) {
                uiState = FlixUiState.Error("Failed to create: ${e.message}")
            }
        }
    }

    fun createFlix(flix: Flix) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            try {
                flix.poster?.let { uploadFlixFile(it) }
                flix.video?.let { uploadFlixFile(it) }
                //TODO perform api request
                uiState = FlixUiState.Success
            } catch (e: Exception) {
                uiState = FlixUiState.Error("Failed to create: ${e.message}")
            }
        }
    }

    private suspend fun uploadFlixFile(path: String) {
        delay(500)
        //TODO perform api request
        uiState = FlixUiState.Success
        if ("fail" in path) throw IOException("Upload failed for $path")
    }

    fun downloadFlix(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            //TODO perform api request
            uiState = FlixUiState.Success
        }
    }

    fun watchFlix(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            //TODO perform api request
            uiState = FlixUiState.Success
        }
    }

    fun getFlix(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = FlixUiState.Loading
            //TODO perform api request
            uiState = FlixUiState.Success
        }
    }

    fun clearState() {
        uiState = FlixUiState.Idle
    }
}

sealed class FlixUiState {
    object Idle : FlixUiState()
    object Loading : FlixUiState()
    object Success : FlixUiState()
    data class Error(val message: String) : FlixUiState()
}
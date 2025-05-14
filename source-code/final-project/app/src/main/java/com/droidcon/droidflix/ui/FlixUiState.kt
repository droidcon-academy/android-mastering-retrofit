package com.droidcon.droidflix.ui

import com.droidcon.droidflix.data.model.FlixErrorResponse

sealed class FlixUiState {
    object Idle : FlixUiState()
    object Loading : FlixUiState()
    object Success : FlixUiState()
    object Unauthorized : FlixUiState()
    data class ApiError(val error: FlixErrorResponse?) : FlixUiState()
    data class Error(val message: String) : FlixUiState()
}
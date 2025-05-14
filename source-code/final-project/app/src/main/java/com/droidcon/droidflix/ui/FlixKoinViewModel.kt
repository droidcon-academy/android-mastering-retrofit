package com.droidcon.droidflix.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.FlixPagingSource
import com.droidcon.droidflix.data.model.Flix
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class FlixKoinViewModel(
    private val api: FlixApi
) : ViewModel() {

    var uiState by mutableStateOf<FlixUiState>(FlixUiState.Idle)
        private set

    fun getFlix(query: String): Flow<PagingData<Flix>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = { FlixPagingSource(api, query) }
        ).flow.cachedIn(viewModelScope)
    }
}

val viewModelModule = module {
    viewModel{ FlixKoinViewModel(get()) }
}
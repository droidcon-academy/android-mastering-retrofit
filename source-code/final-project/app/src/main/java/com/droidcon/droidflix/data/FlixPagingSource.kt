package com.droidcon.droidflix.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.droidcon.droidflix.data.model.Flix

class FlixPagingSource(
    private val flixApi: FlixApi,
    private val query: String
) : PagingSource<Int, Flix>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Flix> {
        val page = params.key ?: 1
        return try {
            val response = flixApi.searchFlix(query, page)
            val flixList = response.body() ?: emptyList()
            LoadResult.Page(
                data = flixList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (flixList.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Flix>): Int? {
        return state.anchorPosition
    }
}
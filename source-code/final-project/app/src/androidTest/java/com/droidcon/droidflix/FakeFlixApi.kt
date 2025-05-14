package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.model.Flix
import com.droidcon.droidflix.data.model.FlixUploadResponse
import com.droidcon.droidflix.data.model.Rating
import okhttp3.MultipartBody
import retrofit2.Response

class FakeFlixApi : FlixApi {

    override suspend fun getFlix(id: String): Response<Flix> {
        val fakeFlix = Flix(
            id = id,
            title = "Fake Title",
            year = "2025",
            plot = "Fake plot for testing.",
            poster = "https://fake.poster",
            video = "https://fake.video",
            ratings = listOf(Rating("IMDB", "10/10"))
        )
        return Response.success(fakeFlix)
    }

    override suspend fun searchFlix(
        query: String,
        page: Int
    ): Response<List<Flix>> {
        TODO("Not yet implemented")
    }

    override suspend fun addFlix(flix: Flix): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun editFlix(
        id: String,
        title: String,
        year: String,
        plot: String,
        poster: String?,
        video: String?
    ): Response<String> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFile(
        url: String,
        filePart: MultipartBody.Part,
        token: String
    ): Response<FlixUploadResponse> {
        TODO("Not yet implemented")
    }
}
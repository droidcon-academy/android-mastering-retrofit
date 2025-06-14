package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.model.Flix
import com.droidcon.droidflix.data.model.FlixUploadResponse
import com.droidcon.droidflix.data.model.Rating
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

    override suspend fun searchFlix(query: String, page: Int): Response<List<Flix>> {
        val fakeResults = listOf(
            Flix(
                id = "1",
                title = "Search Result 1",
                year = "2025",
                plot = "Plot for result 1",
                poster = null,
                video = null,
                ratings = emptyList()
            ),
            Flix(
                id = "2",
                title = "Search Result 2",
                year = "2024",
                plot = "Plot for result 2",
                poster = null,
                video = null,
                ratings = emptyList()
            )
        )
        return Response.success(fakeResults)
    }

    override suspend fun addFlix(flix: Flix): Response<Unit> {
        // Simulate success
        return Response.success(Unit)
    }

    override suspend fun editFlix(
        id: String,
        title: String,
        year: String,
        plot: String,
        poster: String?,
        video: String?
    ): Response<String> {
        // Simulate successful update
        return Response.success(id)
    }

    override suspend fun uploadFile(
        filePart: MultipartBody.Part,
        token: String
    ): Response<FlixUploadResponse> {
        val response = FlixUploadResponse(status = "Uploaded")
        return Response.success(response)
    }

    override suspend fun downloadFile(fileUrl: String): Response<ResponseBody> {
        val body = ResponseBody.create(null, "Fake file content")
        return Response.success(body)
    }
}
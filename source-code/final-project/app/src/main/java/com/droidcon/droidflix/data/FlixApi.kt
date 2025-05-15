package com.droidcon.droidflix.data

import androidx.annotation.Nullable
import com.droidcon.droidflix.data.model.Flix
import com.droidcon.droidflix.data.model.FlixUploadResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FlixApi {

    @GET("flixById/{id}")
    suspend fun getFlix(@Path("id") id: String): Response<Flix>

    @GET("flixSearch/")
    suspend fun searchFlix(@Query("q") query: String, @Query("page") page: Int): Response<List<Flix>>

    @PUT("flixAdd/")
    suspend fun addFlix(@Body flix: Flix): Response<Unit>

    @FormUrlEncoded
    @POST("flixEdit/")
    suspend fun editFlix(
        @Field("id") id: String,
        @Field("title") title: String,
        @Field("year") year: String,
        @Field("plot") plot: String,
        @Nullable @Field("poster") poster: String?,
        @Nullable @Field("video") video: String?,
    ): Response<String>

    @Multipart
    @POST("upload/")
    suspend fun uploadFile(
        @Part filePart: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<FlixUploadResponse>

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}
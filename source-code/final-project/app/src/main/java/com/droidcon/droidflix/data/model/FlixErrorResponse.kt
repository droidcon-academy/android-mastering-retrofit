package com.droidcon.droidflix.data.model

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response

data class FlixErrorResponse(
    val error: String,
    val code: Int
)

fun <T> Response<*>.parseErrorBody(converter: Converter<ResponseBody, T>): T? {
    val errorBody = this.errorBody() ?: return null
    return try {
        converter.convert(errorBody)
    } catch (_: Exception) {
        null
    }
}
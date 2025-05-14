package com.droidcon.droidflix.data

import com.droidcon.droidflix.data.model.FlixUploadResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class SimpleXmlStatusConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == FlixUploadResponse::class.java) {
            Converter<ResponseBody, FlixUploadResponse> { body ->
                val xml = body.string()
                val status = Regex("<status>(.*?)</status>")
                    .find(xml)?.groupValues?.get(1)
                    ?: throw IOException("Invalid XML")
                FlixUploadResponse(status)
            }
        } else null
    }
}
package com.droidcon.droidflix.data

import android.content.Context
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object MockServerManager {

    private val server = MockWebServer()

    val baseUrl get() = server.url("/")

    fun start(context: Context) {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)

                return when {
                    path.startsWith("/flixById/") -> {
                        val id = path.removePrefix("/flixById/")
                        if (id.isBlank()) return MockResponse().setResponseCode(400)
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(getMockDataFromFile("mock/flixById/200.json", context))
                    }

                    path.startsWith("/flixSearch/") -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(buildSearchResponse())
                    }

                    request.getHeader("Authorization") != "Basic username:password" -> {
                        MockResponse().setResponseCode(401)
                    }

                    path == "/flixAdd/" && request.getHeader("Authorization") == "Basic username:password" -> {
                        MockResponse().setResponseCode(200)
                    }

                    path == "/flixEdit/" && request.getHeader("Authorization") == "Basic username:password"  -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("SUCCESS")
                    }

                    path == "/upload/" && request.getHeader("Authorization") == "Basic username:password"  -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("<status>SUCCESS</status>")
                            .addHeader("Content-Type", "application/xml")
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        server.start(8080)
    }

    private val sampleFlixJson = """
        {
            "id": "1",
            "title": "Sample Movie",
            "year": "2024",
            "plot": "A test movie for mock server.",
            "poster": "https://www.omdbapi.com/src/poster.jpg",
            "video": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "ratings": [{"source": "IMDB", "value": "8.5/10"}]
        }
    """.trimIndent()

    fun buildSearchResponse(): String {
        return List(10) {
            sampleFlixJson
        }.joinToString(",", prefix = "[", postfix = "]")
    }

    private fun getMockDataFromFile(fileName: String, context: Context): String {
        var inputStream: InputStream? = null
        var reader: InputStreamReader? = null
        try {
            inputStream = context.assets.open(fileName)
            val builder = StringBuilder()
            reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }

            return builder.toString()
        } catch (e: IOException) {
            throw e
        } finally {
            inputStream?.close()
            reader?.close()
        }
    }

    fun shutdown() {
        server.shutdown()
    }
}
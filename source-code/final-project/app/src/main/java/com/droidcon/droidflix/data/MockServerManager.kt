package com.droidcon.droidflix.data

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object MockServerManager {

    private val server = MockWebServer()

    val baseUrl get() = server.url("/")

    fun start() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: return MockResponse().setResponseCode(404)

                return when {
                    path.startsWith("/flixById/") -> {
                        val id = path.removePrefix("/flixById/")
                        if (id.isBlank()) return MockResponse().setResponseCode(400)
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(sampleFlixJson)
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

    //TODO move this to a file
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

    fun shutdown() {
        server.shutdown()
    }
}
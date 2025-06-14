package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import com.droidcon.droidflix.data.model.Flix
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FlixKoinRepositoryTest : KoinTest {

    private val flixApi: FlixApi by inject()

    @Before
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @Test
    fun testFakeGetFlix() = runTest {
        val response = flixApi.getFlix("1")
        assertTrue(response.isSuccessful)
        assertEquals("Fake Title", response.body()?.title)
    }

    @Test
    fun testFakeSearchFlix() = runTest {
        val response = flixApi.searchFlix("query", 1)
        assertTrue(response.isSuccessful)
        val results = response.body()
        assertNotNull(results)
        assertEquals(2, results.size)
        assertEquals("Search Result 1", results[0].title)
    }

    @Test
    fun testFakeAddFlix() = runTest {
        val flix = Flix(
            id = "99",
            title = "New Flix",
            year = "2023",
            plot = "A new flix for testing.",
            poster = null,
            video = null,
            ratings = emptyList()
        )
        val response = flixApi.addFlix(flix)
        assertTrue(response.isSuccessful)
    }

    @Test
    fun testFakeEditFlix() = runTest {
        val response = flixApi.editFlix(
            id = "42",
            title = "Updated Title",
            year = "2022",
            plot = "Updated plot",
            poster = "https://poster",
            video = "https://video"
        )
        assertTrue(response.isSuccessful)
        assertEquals("42", response.body())
    }

    @Test
    fun testFakeUploadFile() = runTest {
        val filePart = MultipartBody.Part.createFormData(
            "file", "fake.txt",
            "Test file content".toRequestBody()
        )
        val response = flixApi.uploadFile(filePart, token = "token123")
        assertTrue(response.isSuccessful)
        assertEquals("Uploaded", response.body()?.status)
    }

    @Test
    fun testFakeDownloadFile() = runTest {
        val response = flixApi.downloadFile("https://fake.url/file.txt")
        assertTrue(response.isSuccessful)
        val body = response.body()
        assertNotNull(body)
        assertEquals("Fake file content", body.string())
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}
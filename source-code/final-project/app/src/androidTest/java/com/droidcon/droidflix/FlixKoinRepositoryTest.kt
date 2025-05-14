package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @After
    fun tearDown() {
        stopKoin()
    }
}
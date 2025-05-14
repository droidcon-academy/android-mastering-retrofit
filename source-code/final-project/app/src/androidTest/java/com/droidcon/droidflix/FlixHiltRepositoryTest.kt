package com.droidcon.droidflix

import com.droidcon.droidflix.data.FlixApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class FlixHiltRepositoryTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var flixApi: FlixApi

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testFakeGetFlix() = runTest {
        val response = flixApi.getFlix("1")
        assertTrue(response.isSuccessful)
        assertEquals("Fake Title", response.body()?.title)
    }
}
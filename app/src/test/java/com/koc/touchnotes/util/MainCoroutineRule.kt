package com.koc.touchnotes.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
Created by kelvin_clark on 2/20/2021 9:06 AM
 */
@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val testCoroutineDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(testCoroutineDispatcher) {
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
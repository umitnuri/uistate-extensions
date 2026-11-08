package biz.aydin.uistate.demoScreen

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DemoViewModelTest {

    @Test
    fun `Given a fresh DemoViewModel, when time advances, then it rotates through all states`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = ViewModelStore()
            val viewModel = ViewModelProvider(store, ViewModelProvider.NewInstanceFactory())[DemoViewModel::class.java]

            val emissions = mutableListOf<DemoScreenState>()
            val collector: Job = collectState(viewModel.state, emissions)

            advanceTimeBy(6_001L)
            runCurrent()

            val actual = emissions.toList()
            collector.cancel()
            // The demo state machine loops forever; stop its viewModelScope before teardown.
            store.clear()
            runCurrent()

            assertEquals(
                listOf(
                    "Loading",
                    "Loaded(We've received important information)",
                    "Loading",
                    "Error(This is very serious error!)",
                    "Loading",
                    "DeeplyNested",
                    "Loading",
                ),
                actual.map(::describe),
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    // Error is a plain (non-data) state, so compare structurally.
    private fun describe(state: DemoScreenState): String = when {
        state is DemoScreenState.Loading -> "Loading"
        state is DemoScreenState.Loaded -> "Loaded(${state.data})"
        state is DemoScreenState.Error -> "Error(${state.error})"
        state is DemoScreenState.Nested.DeeplyNested -> "DeeplyNested"
        else -> error("unexpected state")
    }

    private fun <T> TestScope.collectState(
        flow: kotlinx.coroutines.flow.StateFlow<T>,
        sink: MutableList<T>,
    ): Job = launch(UnconfinedTestDispatcher(testScheduler)) {
        flow.collect { sink.add(it) }
    }
}

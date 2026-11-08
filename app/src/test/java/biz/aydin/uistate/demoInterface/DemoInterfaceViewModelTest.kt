package biz.aydin.uistate.demoInterface

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
class DemoInterfaceViewModelTest {

    @Test
    fun `Given a fresh DemoInterfaceViewModel, when time advances, then it rotates through all states`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = ViewModelStore()
            val viewModel = ViewModelProvider(store, ViewModelProvider.NewInstanceFactory())[DemoInterfaceViewModel::class.java]

            val emissions = mutableListOf<DemoInterfaceState>()
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
                    "Loaded(Interface: We've received important information)",
                    "Loading",
                    "Error(Interface: This is very serious error!)",
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
    private fun describe(state: DemoInterfaceState): String = when {
        state is DemoInterfaceState.Loading -> "Loading"
        state is DemoInterfaceState.Loaded -> "Loaded(${state.data})"
        state is DemoInterfaceState.Error -> "Error(${state.error})"
        state is DemoInterfaceState.Nested.DeeplyNested -> "DeeplyNested"
        else -> error("unexpected state")
    }

    private fun <T> TestScope.collectState(
        flow: kotlinx.coroutines.flow.StateFlow<T>,
        sink: MutableList<T>,
    ): Job = launch(UnconfinedTestDispatcher(testScheduler)) {
        flow.collect { sink.add(it) }
    }
}

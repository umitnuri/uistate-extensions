package biz.aydin.uistate.demoScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DemoViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    private val mutableState: MutableStateFlow<DemoScreenState> =
        MutableStateFlow(DemoScreenState.Loading)

    val state: StateFlow<DemoScreenState> = mutableState
        .stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DemoScreenState.Loading
        )

    init {
        launch {
            while (true) {
                delay(1000)
                mutableState.value = DemoScreenState.Loaded("We've received important information")
                delay(1000)
                mutableState.value = DemoScreenState.Loading
                delay(1000)
                mutableState.value = DemoScreenState.Error(error = "This is very serious error!")
                delay(1000)
                mutableState.value = DemoScreenState.Loading
            }
        }
    }
}

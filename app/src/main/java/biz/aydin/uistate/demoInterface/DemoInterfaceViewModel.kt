package biz.aydin.uistate.demoInterface

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

class DemoInterfaceViewModel : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    private val mutableState: MutableStateFlow<DemoInterfaceState> =
        MutableStateFlow(DemoInterfaceState.Loading)

    val state: StateFlow<DemoInterfaceState> = mutableState
        .stateIn(
            scope = this,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DemoInterfaceState.Loading
        )

    init {
        launch {
            while (true) {
                delay(1000)
                mutableState.value = DemoInterfaceState.Loaded("Interface: We've received important information")
                delay(1000)
                mutableState.value = DemoInterfaceState.Loading
                delay(1000)
                mutableState.value = DemoInterfaceState.Error(error = "Interface: This is very serious error!")
                delay(1000)
                mutableState.value = DemoInterfaceState.Loading
                delay(1000)
                mutableState.value = DemoInterfaceState.Nested.DeeplyNested
                delay(1000)
                mutableState.value = DemoInterfaceState.Loading
            }
        }
    }
}


package biz.aydin.uistate.demoInterface

import biz.aydin.annotation.UIState

@UIState
sealed interface DemoInterfaceState {
    data object Loading : DemoInterfaceState
    data class Loaded(val data: String) : DemoInterfaceState
    data class Error(val error: String) : DemoInterfaceState

    sealed interface Nested : DemoInterfaceState {
        data object DeeplyNested : Nested
    }
}


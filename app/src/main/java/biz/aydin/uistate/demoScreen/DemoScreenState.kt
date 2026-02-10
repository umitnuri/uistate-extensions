package biz.aydin.uistate.demoScreen

import biz.aydin.annotation.UIState

@UIState
sealed class DemoScreenState {
    data object Loading : DemoScreenState()
    data class Loaded(val data: String) : DemoScreenState()
    class Error(val error: String) : DemoScreenState()

    sealed class Nested : DemoScreenState() {
        data object DeeplyNested : Nested()
    }
}

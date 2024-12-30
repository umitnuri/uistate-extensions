package biz.aydin.uistate.processor.test

import biz.aydin.annotation.UIState

@UIState
sealed class TestState {
    data object Loading : TestState()
    class Error(val error: String) : TestState()
    data class Success(val data: String) : TestState()
}

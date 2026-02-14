package biz.aydin.uistate.demoInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.deeplyNested
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.error
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.invoke
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loaded
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loading
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.nested

@Composable
fun DemoInterfaceScreen(
    viewModel: DemoInterfaceViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state {
        error {
            DemoInterfaceScreenError(error = error)
        }
        loaded {
            DemoInterfaceScreenLoaded(data = data)
        }
        loading {
            DemoInterfaceScreenLoading()
        }
        nested {
            deeplyNested {
                DemoInterfaceScreenDeeplyNested()
            }
        }
    }
}

@Composable
fun DemoInterfaceScreenLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Interface Loading...", fontSize = 48.sp)
    }
}

@Composable
fun DemoInterfaceScreenLoaded(
    data: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Interface Loaded...", fontSize = 48.sp)
        Text(text = data, fontSize = 24.sp, textAlign = TextAlign.Justify)
    }
}

@Composable
fun DemoInterfaceScreenError(
    error: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Interface Error...", fontSize = 48.sp)
        Text(text = error, fontSize = 24.sp)
    }
}

@Composable
fun DemoInterfaceScreenNested() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Interface Nested...", fontSize = 48.sp)
        Text(text = "Generic Nested State", fontSize = 24.sp)
    }
}

@Composable
fun DemoInterfaceScreenDeeplyNested() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Magenta),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Interface Deeply Nested...", fontSize = 48.sp)
    }
}

@Preview
@Composable
private fun DemoInterfaceScreenLoadingPreview() {
    DemoInterfaceScreenLoading()
}

@Preview
@Composable
private fun DemoInterfaceScreenLoadedPreview() {
    DemoInterfaceScreenLoaded(data = "Data")
}

@Preview
@Composable
private fun DemoInterfaceScreenErrorPreview() {
    DemoInterfaceScreenError(error = "Error")
}

@Preview
@Composable
private fun DemoInterfaceScreenNestedPreview() {
    DemoInterfaceScreenNested()
}

@Preview
@Composable
private fun DemoInterfaceScreenDeeplyNestedPreview() {
    DemoInterfaceScreenDeeplyNested()
}


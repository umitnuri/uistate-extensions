package biz.aydin.uistate.demoScreen

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
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.error
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.invoke
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loaded
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loading

@Composable
fun DemoScreen(
    viewModel: DemoViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state{
        error {
            DemoScreenError(error = error)
        }
        loaded {
            DemoScreenLoaded(data = data)
        }
        loading {
            DemoScreenLoading()
        }
    }
}

@Composable
fun DemoScreenLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loading...", fontSize = 48.sp)
    }
}

@Composable
fun DemoScreenLoaded(
    data: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Loaded...", fontSize = 48.sp)
        Text(text = data, fontSize = 24.sp, textAlign = TextAlign.Justify)
    }
}

@Composable
fun DemoScreenError(
    error: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error...", fontSize = 48.sp)
        Text(text = error, fontSize = 24.sp)
    }
}

@Preview
@Composable
private fun DemoScreenLoadingPreview() {
    DemoScreenLoading()
}

@Preview
@Composable
private fun DemoScreenLoadedPreview() {
    DemoScreenLoaded(data = "Data")
}

@Preview
@Composable
private fun DemoScreenErrorPreview() {
    DemoScreenError(error = "Error")
}

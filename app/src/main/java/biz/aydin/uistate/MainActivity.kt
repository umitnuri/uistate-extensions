package biz.aydin.uistate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import biz.aydin.uistate.demoScreen.DemoScreen
import biz.aydin.uistate.demoScreen.DemoViewModel

class MainActivity : ComponentActivity() {
    private val demoViewModel: DemoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoScreen(viewModel = demoViewModel)
        }
    }
}

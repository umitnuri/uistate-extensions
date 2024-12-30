# Motivation
My colleague astutely observed that effectively utilising state extensions requires a streamlined enforcement mechanism. Recognizing the validity of this point, I developed this library to simplify the process for other developers. By minimizing boilerplate code, this library aims to enhance developer productivity and reduce the potential for errors.
# Getting Started
This library exclusively supports sealed classes and interfaces. To incorporate the library into your project, follow these steps to include the necessary project dependencies.
```
plugin{
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("biz.aydin.library.uistate-extension:annotation:1.0.0")
    ksp("biz.aydin.library.uistate-extension:processor:1.0.0")
}
```
Annotate sealed classes or interfaces that represent UI states with the @UIState annotation.
```

import biz.aydin.annotation.UIState

@UIState
sealed class DemoScreenState {
    data object Loading : DemoScreenState()
    data class Loaded(val data: String) : DemoScreenState()
    class Error(val error: String) : DemoScreenState()
}
```
By annotating the DemoScreenState class with @UIState, the following extension functions will be generated.
```
inline operator fun biz.aydin.uistate.demoScreen.DemoScreenState.invoke(body: biz.aydin.uistate.demoScreen.DemoScreenState.() -> Unit) {
	body()
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.error(body: biz.aydin.uistate.demoScreen.DemoScreenState.Error.() -> Unit): biz.aydin.uistate.demoScreen.DemoScreenState {
	if(this is biz.aydin.uistate.demoScreen.DemoScreenState.Error) body()
	return this
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.loaded(body: biz.aydin.uistate.demoScreen.DemoScreenState.Loaded.() -> Unit): biz.aydin.uistate.demoScreen.DemoScreenState {
	if(this is biz.aydin.uistate.demoScreen.DemoScreenState.Loaded) body()
	return this
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.loading(body: biz.aydin.uistate.demoScreen.DemoScreenState.Loading.() -> Unit): biz.aydin.uistate.demoScreen.DemoScreenState {
	if(this is biz.aydin.uistate.demoScreen.DemoScreenState.Loading) body()
	return this
}

```
Then you can use this in your code like following
```
@Composable
fun DemoScreen(
    viewModel: DemoViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state {
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
```
# Thank you
Please feel free to contribute this work via PR or issues.

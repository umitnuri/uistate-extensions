# UIState Extensions

[![PR Check](https://github.com/umitnuri/uistate-extensions/actions/workflows/pr-check.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/pr-check.yml)
[![Build Test](https://github.com/umitnuri/uistate-extensions/actions/workflows/build.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/build.yml)
[![Publish package to GitHub Packages & Maven Central](https://github.com/umitnuri/uistate-extensions/actions/workflows/release.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/release.yml)

> **📝 See [CHANGELOG.md](CHANGELOG.md) for detailed release notes and change history.**

# Motivation
My colleague astutely observed that effectively utilising state extensions requires a streamlined enforcement mechanism. Recognizing the validity of this point, I developed this library to simplify the process for other developers. By minimizing boilerplate code, this library aims to enhance developer productivity and reduce the potential for errors.
# Getting Started
Here is the blog post where I discuss the pattern [umit.aydin.biz](https://umit.aydin.biz/yet-another-pattern-to-handle-ui-state-in-jetpack-compose)
This library exclusively supports sealed classes and interfaces. To incorporate the library into your project, follow these steps to include the necessary project dependencies.
```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("biz.aydin.library.uistate-extension:annotation:1.0.0")
    ksp("biz.aydin.library.uistate-extension:processor:1.0.0")
}
```
Annotate sealed classes or interfaces that represent UI states with the @UIState annotation.
```kotlin
package biz.aydin.uistate.demoScreen

import biz.aydin.annotation.UIState

@UIState
sealed class DemoScreenState {
    data object Loading : DemoScreenState()
    data class Loaded(val data: String) : DemoScreenState()
    class Error(val error: String) : DemoScreenState()
}
```
By annotating the DemoScreenState class with @UIState, the following extension functions will be generated.
```kotlin
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

## Nested Sealed Hierarchies

The library supports nested sealed classes and interfaces. Extensions are generated for all levels of the hierarchy, with each extension using its **immediate parent** as the receiver type:

```kotlin
@UIState
sealed class DemoScreenState {
    data object Loading : DemoScreenState()
    data class Loaded(val data: String) : DemoScreenState()
    class Error(val error: String) : DemoScreenState()

    sealed class Nested : DemoScreenState() {
        data object DeeplyNested : Nested()
    }
}
```

This generates:
- `loading()`, `loaded()`, `error()`, `nested()` - extensions on `DemoScreenState` (for direct children)
- `deeplyNested()` - extension on `Nested` (uses immediate parent as receiver)

**Important**: Nested extensions must be called within their parent's scope:

```kotlin
state {
    loading { /* ... */ }
    loaded { /* ... */ }
    error { /* ... */ }
    nested {
        // deeplyNested is only available within nested scope
        deeplyNested { /* ... */ }
    }
}
```

This provides better type safety and ensures extensions are only available on appropriate receiver types.

## Usage

**Note that custom extensions are generated in a subpackage ending with `.extensions` to strictly enforce the pattern.**

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Import the generated extensions
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.error
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.invoke
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loaded
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loading
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.nested
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.deeplyNested

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
        nested {
            deeplyNested {
                DemoScreenDeeplyNested()
            }
            // Show Nested screen if not DeeplyNested
            if (this !is DemoScreenState.Nested.DeeplyNested) {
                DemoScreenNested()
            }
        }
    }
}
```

## Sealed Interface Example

The library works identically with sealed interfaces:

```kotlin
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
```

The same extension functions will be generated, with the only difference being the package name:

```kotlin
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.error
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.invoke
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loaded
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loading
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.nested
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.deeplyNested

@Composable
fun DemoInterfaceScreen(viewModel: DemoInterfaceViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state {
        loading { DemoInterfaceScreenLoading() }
        loaded { DemoInterfaceScreenLoaded(data = data) }
        error { DemoInterfaceScreenError(error = error) }
        nested {
            deeplyNested { DemoInterfaceScreenDeeplyNested() }
        }
    }
}
```

# Thank you
Please feel free to contribute this work via PR or issues.

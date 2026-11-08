# UIState Extensions

![Version](https://img.shields.io/badge/version-1.3.6-blue)
![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)
[![PR Check](https://github.com/umitnuri/uistate-extensions/actions/workflows/pr-check.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/pr-check.yml)
[![Build Test](https://github.com/umitnuri/uistate-extensions/actions/workflows/build.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/build.yml)
[![Publish package to GitHub Packages & Maven Central](https://github.com/umitnuri/uistate-extensions/actions/workflows/release.yml/badge.svg)](https://github.com/umitnuri/uistate-extensions/actions/workflows/release.yml)

A Kotlin Symbol Processing (KSP) library that turns `@UIState`-annotated sealed classes and interfaces into a type-safe DSL for Jetpack Compose. Instead of hand-written `when` blocks, each state becomes a generated inline extension function that executes only when the current state matches.

> See [CHANGELOG.md](CHANGELOG.md) for release notes and change history.

## Table of Contents

- [Why](#why)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Nested sealed hierarchies](#nested-sealed-hierarchies)
- [Sealed interface support](#sealed-interface-support)
- [How it works](#how-it-works)
- [Resources](#resources)
- [Contributing](#contributing)
- [License](#license)

## Why

Rendering UI state in Compose typically means long `when` expressions over sealed types: repetitive at every call site, easy to leave a branch unhandled, and impossible to extend with shared behaviour. UIState Extensions removes that boilerplate by generating one type-safe extension per state at compile time, so state handling becomes declarative, chainable, and hard to get wrong.

## Features

- `@UIState` annotation for sealed classes and sealed interfaces
- Generated `invoke` operator plus inline, chainable extension functions (`loading { }`, `loaded { }`, `error { }`, ...)
- Each extension executes only when the current state matches and returns the same state instance, so blocks can be chained in a single call
- Deep nesting support: extensions are generated recursively for hierarchies such as `Parent.Child.Grandchild`
- Nested extensions take their *immediate parent* as receiver for compile-time type safety
- Works with data objects, data classes, and regular classes
- Strict scoping: extensions are generated in a dedicated `{state}.extensions` subpackage

## Requirements

| Component | Version |
|---|---|
| JDK / Kotlin JVM target | 21 |
| KSP | 2.3.12 (paired with Kotlin 2.4.x; this library is built against Kotlin 2.4.21) |
| Android SDK | Demo app targets `compileSdk = 37`, `minSdk = 30` |
| UI layer | Jetpack Compose (any recent version; demo uses Compose BOM 2026.09.00) |

## Installation

Add the KSP plugin and the two artifacts (annotation + processor):

```kotlin
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("biz.aydin.library.uistate-extension:annotation:1.3.6")
    ksp("biz.aydin.library.uistate-extension:processor:1.3.6")
}
```

Artifacts are published to Maven Central and, as a mirror, to GitHub Packages (`https://maven.pkg.github.com/umitnuri/uistate-extensions`).

## Usage

Annotate a sealed class (or interface) that represents your UI state:

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

Then handle state in a Composable with the generated DSL. Custom extensions are generated in a subpackage ending with `.extensions`, which must be imported explicitly to enforce the pattern:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Import the generated extensions
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.error
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.invoke
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loaded
import biz.aydin.uistate.demoScreen.demoscreenstate.extensions.loading

@Composable
fun DemoScreen(viewModel: DemoViewModel) {
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

Only the blocks whose state matches the current instance execute, and each block exposes the state's own properties as receivers (`error`, `data`, ...).

## Nested sealed hierarchies

The library supports nested sealed classes and interfaces. Extensions are generated for all levels of the hierarchy, and each extension uses its **immediate parent** as the receiver type:

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

- `loading()`, `loaded()`, `error()`, `nested()` — extensions on `DemoScreenState` (direct children)
- `deeplyNested()` — extension on `Nested` (uses the immediate parent as receiver)

Nested extensions must be called within their parent's scope:

```kotlin
state {
    loading { /* ... */ }
    loaded { /* ... */ }
    error { /* ... */ }
    nested {
        // deeplyNested is only available within the nested scope
        deeplyNested {
            DemoScreenDeeplyNested()
        }
        // Show the Nested screen if the state is not DeeplyNested
        if (this !is DemoScreenState.Nested.DeeplyNested) {
            DemoScreenNested()
        }
    }
}
```

Because child states are `is`-instances of their parent types, a parent extension also executes for child states, while child extensions require an exact match. This provides compile-time type safety: extensions are only available on compatible receiver types.

## Sealed interface support

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

The same extensions are generated; only the package name differs:

```kotlin
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.deeplyNested
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.error
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.invoke
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loaded
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.loading
import biz.aydin.uistate.demoInterface.demointerfacestate.extensions.nested

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

## How it works

For every annotated sealed type, the processor generates a `{state}.extensions` package containing:

1. An `inline operator fun invoke(body: State.() -> Unit)` that simply runs the DSL block.
2. One `inline fun State.subclassName(body: Subclass.() -> Unit): State` per subclass, executing `body` only when `this is Subclass` and returning the same instance for chaining.

Function names are camel-cased from the subclass name (`Loading` → `loading()`, `TwoWords` → `twoWords()`), and nested subclasses are referenced by fully qualified names.

<details>
<summary>Example of generated code for <code>DemoScreenState</code></summary>

```kotlin
inline operator fun biz.aydin.uistate.demoScreen.DemoScreenState.invoke(
    body: biz.aydin.uistate.demoScreen.DemoScreenState.() -> Unit
) {
    body()
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.error(
    body: biz.aydin.uistate.demoScreen.DemoScreenState.Error.() -> Unit
): biz.aydin.uistate.demoScreen.DemoScreenState {
    if (this is biz.aydin.uistate.demoScreen.DemoScreenState.Error) body()
    return this
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.loaded(
    body: biz.aydin.uistate.demoScreen.DemoScreenState.Loaded.() -> Unit
): biz.aydin.uistate.demoScreen.DemoScreenState {
    if (this is biz.aydin.uistate.demoScreen.DemoScreenState.Loaded) body()
    return this
}

inline fun biz.aydin.uistate.demoScreen.DemoScreenState.loading(
    body: biz.aydin.uistate.demoScreen.DemoScreenState.Loading.() -> Unit
): biz.aydin.uistate.demoScreen.DemoScreenState {
    if (this is biz.aydin.uistate.demoScreen.DemoScreenState.Loading) body()
    return this
}
```

</details>

## Resources

- Blog post on the pattern: [Yet another pattern to handle UI state in Jetpack Compose](https://umit.aydin.biz/yet-another-pattern-to-handle-ui-state-in-jetpack-compose)
- The `app/` module contains runnable demos (`DemoViewModel`, `DemoInterfaceViewModel`) and the full test suites referenced in the changelog.

## Contributing

Contributions are welcome via pull requests and issues:

1. Fork the repository and create a feature branch.
2. Keep changes scoped; add or update tests in `app/src/test/` or `processor/src/test/` where applicable.
3. Run `./gradlew build` locally and ensure it passes.
4. Open a pull request against `main` with a clear description.

## License

This project is licensed under the [Apache License 2.0](LICENSE).

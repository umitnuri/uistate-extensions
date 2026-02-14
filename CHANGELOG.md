# Changelog

All notable changes to the UIState Extensions library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- **BREAKING CHANGE: Nested Sealed Class Receiver Type** - Extensions for nested sealed classes now use their immediate parent as the receiver instead of the top-level annotated class
  - **Previous behavior**: For `sealed class A { sealed class B:A { data object C:B }}`, the extension for `C` was `fun A.c(...): A`
  - **New behavior**: The extension for `C` is now `fun B.c(...): B`, using `B` (the immediate parent) as the receiver
  - **Migration**: Nested extension calls must now be placed inside their parent's extension block
    ```kotlin
    // Before:
    state {
        deeplyNested { /* ... */ }
    }
    
    // After:
    state {
        nested {
            deeplyNested { /* ... */ }
        }
    }
    ```
  - This change provides better type safety and more accurate scoping for nested hierarchies
  - Direct children of the top-level class remain unchanged and still use the top-level class as receiver

### Added
- **Data Class for Parent Tracking** - Added `SealedSubclassWithParent` data class to track parent-child relationships in sealed hierarchies
- **Nested Sealed Hierarchy Support** - The processor now recursively generates extensions for all nested sealed classes and interfaces, not just direct subclasses
  - Extensions are generated for deeply nested hierarchies (e.g., `Parent.Child.Grandchild`)
  - Uses fully qualified names to ensure correct type references in generated code
  - Added `findAllSealedSubclassesWithParents()` method to recursively collect all descendants with their immediate parents

- **Sealed Interface Example** - Complete demonstration of sealed interface support
  - Created `DemoInterfaceState` with nested sealed interface hierarchy
  - Added `DemoInterfaceViewModel` with state rotation cycle
  - Added `DemoInterfaceScreen` with 5 distinct UI screens (Loading, Loaded, Error, Nested, DeeplyNested)
  - Added comprehensive unit tests (27 tests) in `DemoInterfaceStateTest`

- **Enhanced Sealed Class Example** - Extended the existing `DemoScreenState` example
  - Added nested sealed class hierarchy (`Nested` with `DeeplyNested`)
  - Updated `DemoViewModel` to cycle through nested states
  - Added UI screens for `DemoScreenNested` and `DemoScreenDeeplyNested`
  - Added preview functions for all new screens

- **Comprehensive Unit Test Coverage**
  - Expanded `TestStateTest` from 11 to 27 tests
  - Expanded `DemoInterfaceStateTest` to 27 comprehensive tests
  - Added tests for:
    - Negative cases (verifying blocks don't execute for wrong states)
    - Nested hierarchy behavior (both `nested` and `deeplyNested` extensions)
    - Polymorphic behavior (child state matching parent extension)
    - Chaining multiple blocks in a single invocation
    - Return value verification (extensions return the same state instance)

- **Enhanced Documentation**
  - Updated README.md with nested sealed hierarchy examples
  - Added complete sealed interface usage example
  - Added import statement examples showing generated package structure
  - Documented the `.extensions` subpackage convention
  - Added side-by-side comparison of sealed class vs sealed interface syntax

- **Test Coverage for Nested Processing** - Added processor unit test
  - New test: `Given nested sealed class hierarchy, when compiling, then it generates extensions for all nested subclasses`
  - Validates that both `Level1` and `Level1.Level2` extensions are generated correctly
  - Updated test expectations to verify that `Level2` extension uses `Level1` as receiver

### Implementation Details
- **StateProcessor.kt** - Updated code generation logic
  - Modified `visitClassDeclaration()` to use `findAllSealedSubclassesWithParents()` instead of `findAllSealedSubclasses()`
  - Updated `generateCamelCasedConvenienceFunctionForSubclass()` to accept the immediate parent as receiver type parameter
  - Changed from using top-level `className` to using each subclass's immediate parent as the receiver type
  - Uses fully qualified names for accurate nested type references

### Fixed
- **Type Safety for Nested Hierarchies** - Extensions now have correct receiver types based on their immediate parent
  - Provides better compile-time type safety
  - Prevents calling nested extensions on incompatible receiver types
  - Example: For `sealed class A { sealed class B:A { data object C:B() } }`, extension `c()` is only available on `B`, not on `A`

## [1.0.0] - Previous Release

### Added
- Initial release with support for sealed classes and interfaces
- KSP-based code generation for UI state extensions
- Generated invoke operator and state-specific extension functions
- Support for data objects, data classes, and regular classes
- Package structure: `{original.package}.{lowercased-state-name}.extensions`
- Camel-cased function names derived from state class names

### Features
- `@UIState` annotation for marking sealed types
- Automatic generation of type-safe DSL for state handling
- Inline extension functions with lambda receivers
- Support for both sealed classes and sealed interfaces
- Integration with Jetpack Compose and StateFlow

---

## Summary of Test Coverage

### Processor Tests (`processor/src/test/`)
- ✅ 15 tests covering all processor functionality (including new nested hierarchy test)
- ✅ Tests for sealed classes and sealed interfaces
- ✅ Tests for nested hierarchies
- ✅ Tests for camelCase naming
- ✅ Tests for multiple state types

### App Unit Tests (`app/src/test/`)
- ✅ `TestStateTest`: 27 comprehensive tests for sealed class extensions
- ✅ `DemoInterfaceStateTest`: 27 comprehensive tests for sealed interface extensions
- ✅ Total: 54 app unit tests + 15 processor tests = **69 total tests**

### Test Categories
1. **Positive Tests** - Verify blocks execute when state matches
2. **Negative Tests** - Verify blocks don't execute when state doesn't match
3. **Nested Hierarchy Tests** - Verify parent and child extensions work correctly
4. **Chaining Tests** - Verify multiple blocks can be chained in one invocation
5. **Return Value Tests** - Verify extensions return the same state instance
6. **Polymorphism Tests** - Verify child states match parent extensions

---

## Development Notes

### Package Structure
Generated extensions follow the pattern:
```
{original.package}.{lowercased-classname}.extensions
```

Example:
- Original: `biz.aydin.uistate.demoScreen.DemoScreenState`
- Generated: `biz.aydin.uistate.demoScreen.demoscreenstate.extensions`

### Naming Convention
Extension functions are camelCased based on the state class name:
- `Loading` → `loading()`
- `Loaded` → `loaded()`
- `TwoWords` → `twoWords()`

### Nested Hierarchy Behavior
When using nested sealed classes/interfaces:
- Child states are `is` instances of their parent types
- Extensions for parent types will execute for child states
- Extensions for child types only execute for exact matches

Example:
```kotlin
val state: DemoState = DemoState.Nested.DeeplyNested
state {
    nested { } // ✅ Executes (DeeplyNested is a Nested)
    deeplyNested { } // ✅ Executes (exact match)
}
```



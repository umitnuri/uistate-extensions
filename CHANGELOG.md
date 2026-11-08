# Changelog

All notable changes to the UIState Extensions library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.3.6 / 1.3.5] - 2026-11-08

### Fixed
- Rolled back KSP from 2.3.13 to 2.3.12 due to a code generation bug in KSP 2.3.13.

### Changed
- GitHub Actions updated to latest majors: `actions/checkout@v7`, `actions/setup-java@v6`, `github/codeql-action/init@v4` and `github/codeql-action/analyze@v4`.
- Android Gradle Plugin 9.5.0 → 9.5.1.

## [1.3.4] - 2026-11-03

### Changed
- KSP 2.3.12 → 2.3.13 (reverted in 1.3.5/1.3.6 due to a code generation regression).

## [1.3.3] - 2026-11-03

### Fixed
- KSP deprecation warnings in the annotation processor: `Resolver.validate()` and `KSVisitorVoid` now opt in via `enableNewFeatures = true`.

### Changed
- Android compile/target SDK raised to 37.
- Dependency refresh: Kotlin 2.4.21, AGP 9.5.0, KSP 2.3.12, Compose BOM 2026.09.00, lifecycle-runtime-ktx 2.11.0, activity-compose 1.13.0, Gradle wrapper 9.6.1, kctfork 0.14.0, maven-publish plugin 0.37.0.

## [1.3.2 / 1.3.1] - 2026-02-14

### Changed
- **BREAKING**: Extensions for nested sealed types now use their *immediate parent* as the receiver instead of the top-level annotated class. For `sealed class A { sealed class B : A { data object C : B() } }`, `fun A.c()` became `fun B.c()`.
  - Migration: call nested extensions inside their parent's extension block — `state { nested { deeplyNested { ... } } }` instead of `state { deeplyNested { ... } }`.
  - Direct children of the top-level class are unchanged.
- CI only: added `RELEASE_SIGNING_ENABLED` flag to the release workflow; adjusted CodeQL java-kotlin build mode.

## [1.3.0] - 2026-02-10

### Added
- Recursive extension generation for deeply nested sealed classes and interfaces (e.g. `Parent.Child.Grandchild`), with fully qualified type references in generated code.
- `SealedSubclassWithParent` data class and `findAllSealedSubclassesWithParents()` in the processor to track parent-child relationships across the hierarchy.
- Nested demo hierarchy (`DemoScreenState.Nested` / `DeeplyNested`) with ViewModel state rotation and UI screens.
- Complete sealed-interface example (`DemoInterfaceState` with `DemoInterfaceViewModel` and five screens).
- Expanded test coverage: 54 app unit tests (positive/negative cases, polymorphism, chaining, return values) and 15 processor tests, including nested-hierarchy generation.
- README documentation for nested hierarchies, sealed interface usage, and the generated `.extensions` subpackage convention.

### Changed
- Dependency refresh: AGP 9.0.0, Kotlin 2.3.10, KSP 2.3.5, Compose BOM 2026.01.01, lifecycle 2.10.0, activity-compose 1.12.3, kctfork 0.12.1, maven-publish plugin 0.35.0.

## [1.2.1 / 1.2.0] - 2025-11-01

### Changed
- Toolchain updates: Kotlin 2.2.21, KSP 2.3.0, AGP 8.13.0, Compose BOM 2025.10.01, lifecycle-runtime-ktx 2.9.4, activity-compose 1.11.0, kctfork 0.11.0, maven-publish plugin 0.34.0.
- CI: Java 21 pinned in workflows, `actions/setup-java@v5`, CodeQL configuration updated, code-scanning autofix patches merged.

## [1.1.0] - 2025-07-10

### Added
- Dependabot configuration, README badges, and a PR-check CI workflow.

### Changed
- Dependency refresh: Kotlin 2.2.0, AGP 8.11.0, KSP 2.2.0-2.0.2, Compose BOM 2025.06.01, lifecycle-runtime-ktx 2.9.1, activity-compose 1.10.1, maven-publish plugin 0.31.0, kctfork 0.8.0.

## [1.0.0] - 2024-12-31

### Added
- Initial release with support for sealed classes and sealed interfaces.
- `@UIState` annotation for marking sealed types.
- KSP-based code generation of type-safe state-handling DSL: generated `invoke` operator and state-specific inline extension functions with lambda receivers.
- Support for data objects, data classes, and regular classes.
- Package structure: `{original.package}.{lowercased-state-name}.extensions`.
- Camel-cased function names derived from state class names.
- Integration with Jetpack Compose and StateFlow.

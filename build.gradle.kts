// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kover)
}

dependencies {
    // merge coverage from all modules into the root (total) reports
    kover(project(":annotation"))
    kover(project(":processor"))
    kover(project(":app"))
}

kover {
    reports {
        total {
            filters {
                excludes {
                    classes(
                        // demo UI layer - covered by instrumented UI tests, not unit coverage
                        "biz.aydin.uistate.MainActivity*",
                        "biz.aydin.uistate.demoScreen.DemoScreenKt*",
                        "biz.aydin.uistate.demoInterface.DemoInterfaceScreenKt*",
                        // KSP-generated extension files; inline code is measured at its call sites
                        "*.extensions.*",
                    )
                }
            }
            html {
                title = "UIState Extensions coverage"
            }
            verify {
                rule("100% line coverage") {
                    minBound(100)
                }
            }
        }
    }
}

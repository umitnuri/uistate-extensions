import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.maven.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI.create("https://maven.pkg.github.com/umitnuri/uistate-extensions")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "biz.aydin.library.uistate-extension",
        artifactId = "annotation",
        version = extra["VERSION"]!!.toString()
    )

    pom {
        name = "UIState Extension Generator"
        description =
            "Generates boliler plate code that is needed for UIState management in Jetpack Compose."
        inceptionYear = "2024"
        url = "https://github.com/umitnuri/uistate-extensions"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://github.com/umitnuri/uistate-extensions/blob/main/LICENSE"
                distribution = "https://github.com/umitnuri/uistate-extensions/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "umitnuri"
                name = "Umit Aydin"
                url = "https://github.com/umitnuri/"
            }
        }
        scm {
            url = "https://github.com/umitnuri/uistate-extensions"
            connection = "scm:git:git://https://github.com/umitnuri/uistate-extensions.git"
            developerConnection =
                "scm:git:ssh://git@https://github.com/umitnuri/uistate-extensions.git"
        }
    }
}

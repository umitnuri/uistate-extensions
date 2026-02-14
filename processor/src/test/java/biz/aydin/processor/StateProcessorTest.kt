package biz.aydin.processor

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspIncremental
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

typealias GeneratedExtensionFile = String

@OptIn(ExperimentalCompilerApi::class, ExperimentalCompilerApi::class)
class StateProcessorTest {
    private val imports = """
        package biz.aydin.annotation.test
        import biz.aydin.annotation.UIState        
    """.trimIndent()

    @Test
    fun `Given the sealed class is not annotated, when compiling, then it should not generate any extension functions`() {
        val result = compileTestFile(
            fileName = "TestScreenState",
            content = """
                $imports
                sealed class TestScreenState { }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        Assert.assertNull(result.second)
    }

    @Test
    fun `Given annotated sealed class, when compiling, then it only generates correct package`() {
        val result = compileTestFile(
            fileName = "TestScreenState",
            content = """
                $imports
                @UIState
                sealed class TestScreenState { }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedPackageName = """
            package biz.aydin.annotation.test.testscreenstate.extensions
        """.trimIndent().trimIndent()
        assertTrue(result.second!!.contains(expectedPackageName))
    }

    @Test
    fun `Given annotated sealed interface, when compiling, then it only generates correct package`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedPackageName = """
            package biz.aydin.annotation.test.mainscreenstate.extensions
        """.trimIndent().trimIndent()
        assertTrue(result.second!!.contains(expectedPackageName))
    }

    @Test
    fun `Given annotated sealed class has no subclass, when compiling, then it only generates invoke function`() {
        val result = compileTestFile(
            fileName = "TestScreenState",
            content = """
                $imports
                @UIState
                sealed class TestScreenState { }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedResult = """
            package biz.aydin.annotation.test.testscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.TestScreenState.invoke(body: biz.aydin.annotation.test.TestScreenState.() -> Unit) {
            	body()
            }

        """.trimIndent().trimIndent()
        assertEquals(expectedResult, result.second)
    }

    @Test
    fun `Given annotated sealed interface has no subclass, when compiling, then it only generates invoke function`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedResult = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

        """.trimIndent().trimIndent()
        assertEquals(expectedResult, result.second)
    }

    @Test
    fun `Given annotated sealed class has an object subclass, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed class MainScreenState { 
                    object Loading : MainScreenState()
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed interface has an object subclass, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { 
                    object Loading : MainScreenState
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed class has a class subclass, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed class MainScreenState { 
                    class Loading() : MainScreenState()
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed interface has a class subclass, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { 
                    class Loading() : MainScreenState
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed class has multiple types as subclasses, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed class MainScreenState { 
                    class Loading() : MainScreenState()
                    data class Loaded(val data: String) : MainScreenState()
                    object Error : MainScreenState()
                    data object Empty : MainScreenState()
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.empty(body: biz.aydin.annotation.test.MainScreenState.Empty.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Empty) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.error(body: biz.aydin.annotation.test.MainScreenState.Error.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Error) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loaded(body: biz.aydin.annotation.test.MainScreenState.Loaded.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loaded) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        println(result.second)
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed interface has multiple types as subclasses, when compiling, then it generates extension functions`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { 
                    class Loading() : MainScreenState
                    data class Loaded(val data: String) : MainScreenState
                    object Error : MainScreenState
                    data object Empty : MainScreenState
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFile = """
            package biz.aydin.annotation.test.mainscreenstate.extensions

            inline operator fun biz.aydin.annotation.test.MainScreenState.invoke(body: biz.aydin.annotation.test.MainScreenState.() -> Unit) {
            	body()
            }

            inline fun biz.aydin.annotation.test.MainScreenState.empty(body: biz.aydin.annotation.test.MainScreenState.Empty.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Empty) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.error(body: biz.aydin.annotation.test.MainScreenState.Error.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Error) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loaded(body: biz.aydin.annotation.test.MainScreenState.Loaded.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loaded) body()
            	return this
            }

            inline fun biz.aydin.annotation.test.MainScreenState.loading(body: biz.aydin.annotation.test.MainScreenState.Loading.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.Loading) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertEquals(expectedGeneratedFile, result.second)
    }

    @Test
    fun `Given annotated sealed class has a subclass with 2 words, when compiling, then it generates extension with camelCased name`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed class MainScreenState { 
                    object TwoWords : MainScreenState()
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFunction = """
            inline fun biz.aydin.annotation.test.MainScreenState.twoWords(body: biz.aydin.annotation.test.MainScreenState.TwoWords.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.TwoWords) body()
            	return this
            }
        """.trimIndent().trimIndent()
        assertTrue(result.second!!.contains(expectedGeneratedFunction))
    }

    @Test
    fun `Given annotated sealed interface has a subclass with 2 words, when compiling, then it generates extension with camelCased name`() {
        val result = compileTestFile(
            fileName = "MainScreenState",
            content = """
                $imports
                @UIState
                sealed interface MainScreenState { 
                    object TwoWords : MainScreenState
                }
            """.trimIndent().trimIndent()
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)
        val expectedGeneratedFunction = """
            inline fun biz.aydin.annotation.test.MainScreenState.twoWords(body: biz.aydin.annotation.test.MainScreenState.TwoWords.() -> Unit): biz.aydin.annotation.test.MainScreenState {
            	if (this is biz.aydin.annotation.test.MainScreenState.TwoWords) body()
            	return this
            }
        """.trimIndent().trimIndent()
        println(result.second)
        assertTrue(result.second!!.contains(expectedGeneratedFunction))
    }

    @Test
    fun `Given nested sealed class hierarchy, when compiling, then it generates extensions for all nested subclasses`() {
        val result = compileTestFile(
            fileName = "NestedState",
            content = """
                $imports
                @UIState
                sealed class NestedState {
                    sealed class Level1 : NestedState() {
                        data object Level2 : Level1()
                    }
                }
            """.trimIndent()
        )
        // Check comparison result code
        assertEquals(KotlinCompilation.ExitCode.OK, result.first.exitCode)

        // Check for Level1 extension - uses NestedState as receiver (direct child)
        val expectedLevel1 = """
            inline fun biz.aydin.annotation.test.NestedState.level1(body: biz.aydin.annotation.test.NestedState.Level1.() -> Unit): biz.aydin.annotation.test.NestedState {
            	if (this is biz.aydin.annotation.test.NestedState.Level1) body()
            	return this
            }
        """.trimIndent()

        // Check for Level2 extension - uses Level1 as receiver (immediate parent)
        val expectedLevel2 = """
            inline fun biz.aydin.annotation.test.NestedState.Level1.level2(body: biz.aydin.annotation.test.NestedState.Level1.Level2.() -> Unit): biz.aydin.annotation.test.NestedState.Level1 {
            	if (this is biz.aydin.annotation.test.NestedState.Level1.Level2) body()
            	return this
            }
        """.trimIndent()

        // Ensure both are present
        assertTrue(result.second!!.contains(expectedLevel1))
        assertTrue(result.second!!.contains(expectedLevel2))
    }

    private fun compileTestFile(
        fileName: String,
        content: String
    ): Pair<JvmCompilationResult, GeneratedExtensionFile?> {
        val kotlinSource = SourceFile.kotlin(
            name = "$fileName.kt",
            contents = content
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)
            configureKsp {
                symbolProcessorProviders += StateProcessorProvider()
            }
            inheritClassPath = true
            kspIncremental = false
            jdkHome = File(System.getenv("JAVA_HOME"))
        }
        val result: JvmCompilationResult = compilation.compile()
        val generatedExtensionFile =
            result.kspGeneratedSources.firstOrNull() { it.name.contains("${fileName}Extensions") }
                ?.readText()?.trimIndent()

        return result to generatedExtensionFile
    }

    private val JvmCompilationResult.workingDir: File
        get() = outputDirectory.parentFile!!

    private val JvmCompilationResult.kspGeneratedSources: List<File>
        get() {
            val kspWorkingDir = workingDir.resolve("ksp")
            val kspGeneratedDir = kspWorkingDir.resolve("sources")
            val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
            val resourcesGeneratedDir = kspGeneratedDir.resolve("resources")
            return kotlinGeneratedDir.walkTopDown().toList() + resourcesGeneratedDir.walkTopDown()
        }
}
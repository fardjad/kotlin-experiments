@file:OptIn(ExperimentalCompilerApi::class)

package com.fardjad.learning.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class MyProcessorProviderTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `generate data class from sealed interface`() {
        val kotlinSource = SourceFile.kotlin(
            "Test.kt", """
                package com.fardjad.learning.ksp

                data class Something(val prop1: String, val prop2: Int)
                
                @MyAnnotation
                sealed interface TestData {
                    val prop1: String
                    val prop2: Int
                    val prop3: List<String>
                    val prop4: Set<Something>
                }"""
        )

        val compilationResult = compile(tempDir, kotlinSource)
        assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        assertSourceEquals(
            """
                package com.fardjad.learning.ksp
                
                import kotlin.Int
                import kotlin.String
                import kotlin.collections.List
                import kotlin.collections.Set
                
                public data class TestDataImpl(
                  override val prop1: String,
                  override val prop2: Int,
                  override val prop3: List<String>,
                  override val prop4: Set<Something>,
                ) : TestData
            """,
            compilationResult.sourceFor("TestDataImpl.kt")
        )
    }
}
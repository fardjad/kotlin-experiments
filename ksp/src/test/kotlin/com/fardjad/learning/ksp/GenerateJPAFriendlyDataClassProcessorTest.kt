@file:OptIn(ExperimentalCompilerApi::class)

package com.fardjad.learning.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private fun GenerateJPAFriendlyDataClassProcessorTest.testCompilationResult(fixtureName: String) {
    val kotlinSource = SourceFile.kotlin("input.kt", loadResource("${fixtureName}.input.kt"))

    val compilationResult = compile(
        provider = GenerateJPAFriendlyDataClassProcessorProvider(),
        workingDir = tempDir,
        options = mapOf(),
        kotlinSource
    )
    assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
    assertCodeEquals(
        loadResource("${fixtureName}.expected.kt"),
        compilationResult.sourceFor("TestDataJpaFriendly.kt")
    )
}

private fun GenerateJPAFriendlyDataClassProcessorTest.testCompileError(fixtureName: String, errorMessage: String) {
    val kotlinSource = SourceFile.kotlin("input.kt", loadResource("${fixtureName}.input.kt"))

    val compilationResult = compile(
        provider = GenerateJPAFriendlyDataClassProcessorProvider(),
        workingDir = tempDir,
        options = mapOf(),
        kotlinSource
    )
    assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, compilationResult.exitCode)
    assertTrue(compilationResult.messages.contains(errorMessage))
}

private fun GenerateJPAFriendlyDataClassProcessorTest.loadResource(path: String) =
    GenerateJPAFriendlyDataClassProcessorTest::class.java.classLoader.getResourceAsStream("GenerateJPAFriendlyDataClassProcessor/$path")
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: throw IllegalArgumentException("Resource not found: $path")

class GenerateJPAFriendlyDataClassProcessorTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `data class without any id`() = testCompileError(
        "data-class-without-id",
        "There are no ID properties in com.fardjad.learning.ksp.TestData"
    )

    @TestFactory
    fun `test compilation`(): List<DynamicTest> {
        val resource =
            GenerateJPAFriendlyDataClassProcessorTest::class.java.classLoader.getResource("GenerateJPAFriendlyDataClassProcessor")
                ?: throw IllegalArgumentException("Directory not found: MyProcessorProviderTest")

        val testCases = Files.list(Paths.get(resource.toURI()))
            .filter { it.fileName.toString().endsWith(".expected.kt") }
            .map { it.fileName.toString().removeSuffix(".expected.kt") }
            .toList()

        return testCases.map {
            DynamicTest.dynamicTest(it) { testCompilationResult(it) }
        }
    }
}
@file:OptIn(ExperimentalCompilerApi::class)

package com.fardjad.learning.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


private fun GenerateExceptionClassProcessorTest.testCompilationResult(fixtureName: String) {
    val kotlinSource = SourceFile.kotlin("input.kt", loadResource("${fixtureName}.input.kt"))

    val compilationResult = compile(
        provider = GenerateExceptionClassProcessorProvider(),
        workingDir = tempDir,
        options = mapOf(
            "outputPackage" to "com.fardjad.learning.ksp",
        ),
        kotlinSource
    )
    assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
    assertCodeEquals(
        loadResource("${fixtureName}.expected.kt"),
        compilationResult.sourceFor("GeneratedExceptionClasses.kt")
    )
}

private fun GenerateExceptionClassProcessorTest.loadResource(path: String) =
    GenerateJPAFriendlyDataClassProcessorTest::class.java.classLoader.getResourceAsStream("GenerateExceptionClassProcessor/$path")
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: throw IllegalArgumentException("Resource not found: $path")

class GenerateExceptionClassProcessorTest {
    @TempDir
    lateinit var tempDir: Path

    @TestFactory
    fun `test compilation`(): List<DynamicTest> {
        val resource =
            GenerateExceptionClassProcessorTest::class.java.classLoader.getResource("GenerateExceptionClassProcessor")
                ?: throw IllegalArgumentException("Directory not found: GenerateExceptionClassProcessor")

        val testCases = Files.list(Paths.get(resource.toURI()))
            .filter { it.fileName.toString().endsWith(".expected.kt") }
            .map { it.fileName.toString().removeSuffix(".expected.kt") }
            .toList()

        return testCases.map {
            DynamicTest.dynamicTest(it) { testCompilationResult(it) }
        }
    }
}
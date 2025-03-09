@file:OptIn(ExperimentalCompilerApi::class)

package com.fardjad.learning.ksp

import com.tschuchort.compiletesting.*
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.file.Path

fun compile(
    workingDir: Path,
    vararg sourceFiles: SourceFile
) = KotlinCompilation().apply {
    this.sources = sourceFiles.asList()
    this.configureKsp(useKsp2 = true) {
        symbolProcessorProviders.add(MyProcessorProvider())
    }
    this.workingDir = workingDir.toFile()
    this.inheritClassPath = true
    this.verbose = false
    this.messageOutputStream = System.out
}.compile()

fun assertSourceEquals(@Language("kotlin") expected: String, actual: String) =
    assertEquals(expected.trimIndent(), actual.trimIndent())

fun JvmCompilationResult.sourceFor(fileName: String) =
    sourcesGeneratedBySymbolProcessor
        .find { it.name == fileName }
        ?.readText() ?: throw IllegalArgumentException("Could not find file $fileName")

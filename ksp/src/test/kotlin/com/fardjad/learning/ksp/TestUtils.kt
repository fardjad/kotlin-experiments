@file:OptIn(ExperimentalCompilerApi::class)

package com.fardjad.learning.ksp

import com.facebook.ktfmt.format.Formatter
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.file.Path

fun compile(
    provider: SymbolProcessorProvider,
    workingDir: Path,
    options: Map<String, String>,
    vararg sourceFiles: SourceFile
) = KotlinCompilation().apply {
    this.sources = sourceFiles.asList()
    this.configureKsp(useKsp2 = true) {
        symbolProcessorProviders.add(provider)
    }
    this.workingDir = workingDir.toFile()
    this.inheritClassPath = true
    this.verbose = false
    this.messageOutputStream = System.out
    this.kspProcessorOptions = options.toMutableMap()
}.compile()

fun JvmCompilationResult.sourceFor(fileName: String) = sourcesGeneratedBySymbolProcessor
    .find { it.name == fileName }
    ?.readText()
    ?.trimIndent() ?: throw IllegalArgumentException("Could not find file $fileName")

fun assertCodeEquals(expected: String, actual: String) {
    val formattedExpected = Formatter.format(expected)
    val formattedActual = Formatter.format(actual)

    assertEquals(formattedExpected, formattedActual)
}
package com.fardjad.learning.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

private fun TypeSpec.Builder.addRuntimeExceptionConstructors(visibility: Visibility? = null): TypeSpec.Builder {
    val visibilityModifier = when (visibility) {
        Visibility.INTERNAL -> KModifier.INTERNAL
        Visibility.PRIVATE -> KModifier.PRIVATE
        Visibility.PROTECTED -> KModifier.PROTECTED
        else -> KModifier.PUBLIC
    }

    return this
        .addFunction(
            FunSpec.constructorBuilder()
                .addModifiers(visibilityModifier)
                .callSuperConstructor()
                .build()
        )
        .addFunction(
            FunSpec.constructorBuilder()
                .addModifiers(visibilityModifier)
                .addParameter("message", String::class)
                .callSuperConstructor("message")
                .build()
        )
        .addFunction(
            FunSpec.constructorBuilder()
                .addModifiers(visibilityModifier)
                .addParameter("message", String::class)
                .addParameter("cause", Throwable::class)
                .callSuperConstructor("message", "cause")
                .build()
        )
        .addFunction(
            FunSpec.constructorBuilder()
                .addModifiers(visibilityModifier)
                .addParameter("cause", Throwable::class)
                .callSuperConstructor("cause")
                .build()
        )
        .addFunction(
            FunSpec.constructorBuilder()
                .addModifiers(visibilityModifier)
                .addParameter("message", String::class)
                .addParameter("cause", Throwable::class)
                .addParameter("enableSuppression", Boolean::class)
                .addParameter("writableStackTrace", Boolean::class)
                .callSuperConstructor("message", "cause", "enableSuppression", "writableStackTrace")
                .build()
        )
}

private class GenerateExceptionClassProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val outputPackage = options["outputPackage"] ?: run {
            logger.error("outputPackage option is required")
            return emptyList()
        }

        val symbols = resolver.getSymbolsWithAnnotation(GenerateExceptionClass::class.qualifiedName!!)
        val unableToProcess = symbols.filterNot { it.validate() }
        val classDeclarations = symbols.filterIsInstance<KSClassDeclaration>().toList()

        if (classDeclarations.isEmpty()) {
            return unableToProcess.toList()
        }

        val generatedExceptionSealedClass = TypeSpec.classBuilder("GeneratedException")
            .addModifiers(KModifier.SEALED)
            .superclass(RuntimeException::class)
            .addRuntimeExceptionConstructors(visibility = Visibility.PROTECTED)
            .build()

        val generatedExceptionTypeSpecs: MutableList<TypeSpec> = mutableListOf()

        for (classDeclaration in classDeclarations) {
            val annotations = classDeclaration.annotations.filter {
                it.shortName.asString() == GenerateExceptionClass::class.simpleName
            }.toList()

            val names = annotations.mapNotNull { annotation ->
                annotation.arguments.firstOrNull()?.value as? String
            }

            for (nameArg in names) {
                val exceptionTypeSpec = TypeSpec.classBuilder("${classDeclaration.toClassName().simpleName}${nameArg}")
                    .superclass(ClassName(outputPackage, generatedExceptionSealedClass.name!!))
                    .addRuntimeExceptionConstructors()
                    .addOriginatingKSFile(classDeclaration.containingFile!!)
                    .build()

                generatedExceptionTypeSpecs.add(exceptionTypeSpec)
            }
        }

        val fileSpec = FileSpec
            .builder(outputPackage, "GeneratedExceptionClasses")
            .apply {
                addType(generatedExceptionSealedClass)
                generatedExceptionTypeSpecs.forEach { addType(it) }
            }
            .build()

        fileSpec.writeTo(codeGenerator, aggregating = true)

        return unableToProcess.toList()
    }
}

@AutoService(SymbolProcessorProvider::class)
class GenerateExceptionClassProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return GenerateExceptionClassProcessor(
            options = environment.options,
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}

package com.fardjad.learning.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo

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

        val generatedExceptionInterface = TypeSpec.interfaceBuilder("GeneratedException")
            .addModifiers(KModifier.SEALED)
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
                val exceptionTypeSpec = TypeSpec.classBuilder(nameArg)
                    .superclass(RuntimeException::class)
                    .addSuperinterface(ClassName(outputPackage, generatedExceptionInterface.name!!))
                    .addFunction(
                        FunSpec.constructorBuilder()
                            .callSuperConstructor()
                            .build()
                    )
                    .addFunction(
                        FunSpec.constructorBuilder()
                            .addParameter("message", String::class)
                            .callSuperConstructor(CodeBlock.of("message"))
                            .build()
                    )
                    .addFunction(
                        FunSpec.constructorBuilder()
                            .addParameter("message", String::class)
                            .addParameter("cause", Throwable::class)
                            .callSuperConstructor(CodeBlock.of("message, cause"))
                            .build()
                    )
                    .addFunction(
                        FunSpec.constructorBuilder()
                            .addParameter("cause", Throwable::class)
                            .callSuperConstructor(CodeBlock.of("cause"))
                            .build()
                    )
                    .addFunction(
                        FunSpec.constructorBuilder()
                            .addParameter("message", String::class)
                            .addParameter("cause", Throwable::class)
                            .addParameter("enableSuppression", Boolean::class)
                            .addParameter("writableStackTrace", Boolean::class)
                            .callSuperConstructor(CodeBlock.of("message, cause, enableSuppression, writableStackTrace"))
                            .build()
                    )
                    .addOriginatingKSFile(classDeclaration.containingFile!!)
                    .build()

                generatedExceptionTypeSpecs.add(exceptionTypeSpec)
            }
        }

        val fileSpec = FileSpec
            .builder(outputPackage, "GeneratedExceptionClasses")
            .apply {
                addType(generatedExceptionInterface)
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

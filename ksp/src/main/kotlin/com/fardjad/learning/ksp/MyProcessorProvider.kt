package com.fardjad.learning.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

@AutoService(SymbolProcessorProvider::class)
class MyProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MyProcessor(
            options = environment.options,
            codeGenerator = environment.codeGenerator,
            logger = environment.logger
        )
    }
}

private class MyProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(MyAnnotation::class.qualifiedName!!)
        val unableToProcess = symbols.filterNot { it.validate() }

        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(Visitor(), Unit) }

        return unableToProcess.toList()
    }

    private inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit
        ) {
            logger.info("MyProcessor options: $options")

            if (classDeclaration.classKind != ClassKind.INTERFACE) {
                logger.error("@MyAnnotation can only be used on interfaces")
            }

            val dataClassName = "${classDeclaration.simpleName.asString()}Impl"

            val properties = classDeclaration.getAllProperties()
            val constructor = FunSpec.constructorBuilder()
                .addParameters(
                    properties.map { property ->
                        ParameterSpec.builder(property.simpleName.asString(), property.type.resolve().toTypeName())
                            .build()
                    }.toList()
                )
                .build()

            val dataClassImpl = TypeSpec.classBuilder(dataClassName)
                .addModifiers(KModifier.DATA)
                .addSuperinterface(classDeclaration.toClassName())
                .primaryConstructor(constructor)
                .addProperties(
                    properties.map { property ->
                        PropertySpec.builder(property.simpleName.asString(), property.type.resolve().toTypeName())
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer(property.simpleName.asString())
                            .build()
                    }.toList()
                )
                .build()

            val fileSpec = FileSpec
                .builder(
                    packageName = classDeclaration.packageName.asString(),
                    fileName = dataClassName
                )
                .addType(dataClassImpl)
                .build()

            fileSpec.writeTo(
                codeGenerator = codeGenerator,
                aggregating = false
            )
        }
    }
}


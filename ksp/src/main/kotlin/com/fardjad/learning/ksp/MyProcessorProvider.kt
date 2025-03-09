package com.fardjad.learning.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import jakarta.persistence.Id
import org.hibernate.annotations.NaturalId
import kotlin.reflect.KClass

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

private fun KSPropertyDeclaration.hasAnnotation(annotationClass: KClass<out Annotation>, resolver: Resolver): Boolean {
    val annotationDeclaration =
        resolver.getClassDeclarationByName(resolver.getKSNameFromString(annotationClass.qualifiedName!!))
            ?: return false
    return annotations.any { it.annotationType.resolve().declaration == annotationDeclaration }
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
            .forEach { it.accept(Visitor(resolver), Unit) }

        return unableToProcess.toList()
    }

    private inner class Visitor(private val resolver: Resolver) : KSVisitorVoid() {
        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit
        ) {
            logger.info("MyProcessor options: $options")

            if (!classDeclaration.modifiers.contains(Modifier.DATA)) {
                logger.error("@MyAnnotation can only be used on data classes")
            }

            val dataClassName = "${classDeclaration.simpleName.asString()}JpaFriendly"

            val properties = classDeclaration.getAllProperties()

            val naturalIdProps = properties.filter { it.hasAnnotation(NaturalId::class, resolver) }.toList()
            val idProps = properties.filter { it.hasAnnotation(Id::class, resolver) }.toList()

            val keyProps = when {
                naturalIdProps.isNotEmpty() -> naturalIdProps
                idProps.isNotEmpty() -> idProps
                else -> {
                    logger.error("No @NaturalId or @Id properties found in ${classDeclaration.qualifiedName?.asString()}")
                    emptyList()
                }
            }.toList()

            val equalsFunction = FunSpec.builder("equals")
                .addModifiers(KModifier.OVERRIDE, KModifier.FINAL)
                .addParameter("other", Any::class.asTypeName().copy(nullable = true))
                .returns(Boolean::class)
                .apply {
                    addStatement("if (this === other) return true")
                    addStatement("if (other == null) return false")
                    addStatement("val oEffectiveClass = if (other is org.hibernate.proxy.HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass")
                    addStatement("val thisEffectiveClass = if (this is org.hibernate.proxy.HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass")
                    addStatement("if (thisEffectiveClass != oEffectiveClass) return false")
                    addStatement("other as $dataClassName")

                    if (keyProps.isNotEmpty()) {
                        keyProps.forEach { prop ->
                            val name = prop.simpleName.asString()
                            addStatement("if ($name != other.$name) return false")
                        }
                        addStatement("return true")
                    } else {
                        addStatement("return false")
                    }
                }
                .build()

            val hashCodeFunction = FunSpec.builder("hashCode")
                .addModifiers(KModifier.OVERRIDE, KModifier.FINAL)
                .returns(Int::class)
                .apply {
                    addStatement("return if (this is org.hibernate.proxy.HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()")
                }
                .build()

            val toStringFunction = FunSpec.builder("toString")
                .addModifiers(KModifier.OVERRIDE)
                .returns(String::class)
                .apply {
                    val propertiesExpression = keyProps.joinToString(", ") { prop ->
                        val name = prop.simpleName.asString()
                        """$name = $$name"""
                    }

                    if (keyProps.isNotEmpty()) {
                        addStatement("return %L + \"( %L )\"", "this::class.simpleName", propertiesExpression)
                    } else {
                        addStatement("return %L", "\"${classDeclaration.simpleName}\"")
                    }
                }
                .build()

            val constructor = FunSpec.constructorBuilder()
                .addParameters(
                    properties.map { property ->
                        val typeName = property.type.resolve().toTypeName()
                        val defaultValue = if (typeName.isNullable) CodeBlock.of("null") else null

                        ParameterSpec.builder(property.simpleName.asString(), typeName)
                            .apply { if (defaultValue != null) defaultValue(defaultValue) }
                            .build()
                    }.toList()
                )
                .build()

            val dataClassImpl = TypeSpec.classBuilder(dataClassName)
                .addOriginatingKSFile(classDeclaration.containingFile!!)
                .addModifiers(KModifier.DATA)
                .addAnnotations(
                    classDeclaration.annotations.toList()
                        .filterNot {
                            it.annotationType.resolve().declaration == resolver.getClassDeclarationByName(
                                resolver.getKSNameFromString(MyAnnotation::class.qualifiedName!!)
                            )
                        }
                        .map { it.toAnnotationSpec(true) }
                )
                .primaryConstructor(constructor)
                .addProperties(
                    properties.map { property ->
                        PropertySpec.builder(property.simpleName.asString(), property.type.resolve().toTypeName())
                            .addModifiers(KModifier.OPEN)
                            .initializer(property.simpleName.asString())
                            .addAnnotations(
                                property.annotations.toList().map { it.toAnnotationSpec(true) }
                            )
                            .build()
                    }.toList()
                )
                .addFunction(equalsFunction)
                .addFunction(hashCodeFunction)
                .addFunction(toStringFunction)
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


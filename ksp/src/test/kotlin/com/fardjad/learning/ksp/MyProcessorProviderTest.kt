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

                import jakarta.persistence.*

                data class Something(val prop1: String, val prop2: Int)
                
                @MyAnnotation
                @Entity
                @Table(name = "test")
                data class TestData(
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    @Column(name = "id", nullable = false)
                    val id: String?,
                    
                    val prop2: Int,
                    val prop3: List<String>,
                    val prop4: Set<Something>,
                )"""
        )

        val compilationResult = compile(tempDir, kotlinSource)
        assertEquals(KotlinCompilation.ExitCode.OK, compilationResult.exitCode)
        assertSourceEquals(
            """
                package com.fardjad.learning.ksp
                
                import jakarta.persistence.Column
                import jakarta.persistence.Entity
                import jakarta.persistence.GeneratedValue
                import jakarta.persistence.GenerationType
                import jakarta.persistence.Id
                import jakarta.persistence.Table
                import kotlin.Any
                import kotlin.Boolean
                import kotlin.Int
                import kotlin.String
                import kotlin.collections.List
                import kotlin.collections.Set
                
                @Entity
                @Table(name = "test")
                public data class TestDataJpaFriendly(
                  @Id
                  @GeneratedValue(strategy = GenerationType.IDENTITY)
                  @Column(
                    name = "id",
                    nullable = false,
                  )
                  public open val id: String? = null,
                  public open val prop2: Int,
                  public open val prop3: List<String>,
                  public open val prop4: Set<Something>,
                ) {
                  final override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other == null) return false
                    val oEffectiveClass = if (other is org.hibernate.proxy.HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
                    val thisEffectiveClass = if (this is org.hibernate.proxy.HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
                    if (thisEffectiveClass != oEffectiveClass) return false
                    other as TestDataJpaFriendly
                    if (id != other.id) return false
                    return true
                  }
                
                  final override fun hashCode(): Int = if (this is org.hibernate.proxy.HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
                
                  override fun toString(): String = this::class.simpleName + "( id = ${'$'}id )"
                }
            """,
            compilationResult.sourceFor("TestDataJpaFriendly.kt")
        )
    }
}
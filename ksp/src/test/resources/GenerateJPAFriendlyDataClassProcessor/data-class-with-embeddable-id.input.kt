package com.fardjad.learning.ksp

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.Objects

@Embeddable
data class EmbeddableId(
    private val field1: String?,
    private val field2: String?
)

@GenerateJPAFriendlyDataClass
@Entity
@Table(name = "test")
data class TestData(
    @EmbeddedId
    val id: EmbeddableId,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as TestData

        return id != null && id == other.id
    }

    final override fun hashCode(): Int = Objects.hash(id);
}

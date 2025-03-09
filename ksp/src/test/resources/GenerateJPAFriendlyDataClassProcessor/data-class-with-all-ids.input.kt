package com.fardjad.learning.ksp

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.hibernate.proxy.HibernateProxy
import java.util.Objects

@Embeddable
data class EmbeddableId1(
    private val field1: String?,
    private val field2: String?
)

@Embeddable
data class EmbeddableId2(
    private val field1: String?,
    private val field2: String?
)

@GenerateJPAFriendlyDataClass
@Entity
@Table(name = "test")
data class TestData(
    @Id
    val id1: String,

    @Id
    val id2: String,

    @EmbeddedId
    val id3: EmbeddableId1,

    @EmbeddedId
    val id4: EmbeddableId2,

    @NaturalId
    val id5: String,

    @NaturalId
    val id6: String
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

        return id1 == other.id1
                && id3 == other.id3
                && id5 == other.id5
                && id6 == other.id6
    }

    final override fun hashCode(): Int = Objects.hash(id3, id5, id6);

}

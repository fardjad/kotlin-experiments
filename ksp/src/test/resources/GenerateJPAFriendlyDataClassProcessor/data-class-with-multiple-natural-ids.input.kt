package com.fardjad.learning.ksp

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId
import org.hibernate.proxy.HibernateProxy
import java.util.Objects
import java.util.UUID

@GenerateJPAFriendlyDataClass
@Entity
@Table(name = "test")
data class TestData(
    @NaturalId
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id1", nullable = false)
    val id1: UUID? = null,

    @NaturalId
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id2", nullable = false)
    val id2: UUID? = null,
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
                && id2 == other.id2
    }

    final override fun hashCode(): Int = Objects.hash(id1, id2);

}

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
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id1", nullable = false)
    val id: UUID? = null,

    @NaturalId
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

        return id == other.id
                && id2 == other.id2
    }

    final override fun hashCode(): Int = Objects.hash(id2);

}

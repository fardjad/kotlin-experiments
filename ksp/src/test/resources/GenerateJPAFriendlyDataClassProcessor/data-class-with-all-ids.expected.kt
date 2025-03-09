package com.fardjad.learning.ksp

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Objects
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import org.hibernate.annotations.NaturalId
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "test")
public data class TestDataJpaFriendly(
    @Id public open val id1: String,
    @Id public open val id2: String,
    @EmbeddedId public open val id3: EmbeddableId1,
    @EmbeddedId public open val id4: EmbeddableId2,
    @NaturalId public open val id5: String,
    @NaturalId public open val id6: String,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass
            else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass
            else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as TestDataJpaFriendly
        if (id1 != other.id1) return false
        if (id3 != other.id3) return false
        if (id5 != other.id5) return false
        if (id6 != other.id6) return false
        return true
    }

    final override fun hashCode(): Int = Objects.hash(id3, id5, id6)

    override fun toString(): String = this::class.simpleName!!
}

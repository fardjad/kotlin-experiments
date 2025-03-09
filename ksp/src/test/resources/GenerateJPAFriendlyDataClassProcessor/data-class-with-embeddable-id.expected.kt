package com.fardjad.learning.ksp

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.Objects
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "test")
public data class TestDataJpaFriendly(
    @EmbeddedId public open val id: EmbeddableId,
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
        if (id == null) return false
        if (id != other.id) return false
        return true
    }

    final override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String = this::class.simpleName!!
}

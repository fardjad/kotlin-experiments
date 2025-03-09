package com.fardjad.learning.ksp

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "test")
public data class TestDataJpaFriendly(
    @Id
    @GeneratedValue
    @Column(
        name = "id1",
        nullable = false,
    )
    public open val id1: UUID? = null,
    @Id
    @GeneratedValue
    @Column(
        name = "id2",
        nullable = false,
    )
    public open val id2: UUID? = null,
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
        if (id1 == null) return false
        if (id1 != other.id1) return false
        return true
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode()
        else javaClass.hashCode()

    override fun toString(): String = this::class.simpleName!!
}

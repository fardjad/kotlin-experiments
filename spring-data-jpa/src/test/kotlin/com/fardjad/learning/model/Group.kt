package com.fardjad.learning.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "groups_people",
        joinColumns = [JoinColumn(name = "group_name")],
        inverseJoinColumns = [JoinColumn(name = "people_id")]
    )
    val people: MutableSet<Person> = mutableSetOf()
) {

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Group

        return name != null && name == other.name
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  name = $name )"
    }
}

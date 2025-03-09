package com.fardjad.learning.model

import com.fardjad.learning.ksp.MyAnnotation
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
@MyAnnotation
data class Group(
    @Id
    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
    )
    @JoinTable(
        name = "groups_people",
        joinColumns = [JoinColumn(name = "group_name")],
        inverseJoinColumns = [JoinColumn(name = "people_id")]
    )
    val people: MutableSet<PersonJpaFriendly> = mutableSetOf()
)

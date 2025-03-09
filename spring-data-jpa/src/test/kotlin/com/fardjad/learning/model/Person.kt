package com.fardjad.learning.model

import com.fardjad.learning.ksp.MyAnnotation
import jakarta.persistence.*

@Entity
@Table(name = "people")
@MyAnnotation
data class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String
)

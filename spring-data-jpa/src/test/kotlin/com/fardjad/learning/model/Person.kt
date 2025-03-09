package com.fardjad.learning.model

import com.fardjad.learning.ksp.GenerateJPAFriendlyDataClass
import jakarta.persistence.*

@Entity
@Table(name = "people")
@GenerateJPAFriendlyDataClass
data class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String,
)

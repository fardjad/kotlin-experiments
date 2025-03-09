package com.fardjad.learning.ksp

import jakarta.persistence.*

@GenerateJPAFriendlyDataClass
@Entity
@Table(name = "test")
data class TestData(
    @Column(name = "prop", nullable = false)
    val prop: String,
)

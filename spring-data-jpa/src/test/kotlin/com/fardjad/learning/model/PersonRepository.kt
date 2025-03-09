package com.fardjad.learning.model

import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person, Long>
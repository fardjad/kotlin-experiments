package com.fardjad.learning

import com.fardjad.learning.database.PostgreSQLDatabaseExtension
import com.fardjad.learning.model.Group
import com.fardjad.learning.model.GroupRepository
import com.fardjad.learning.model.Person
import com.fardjad.learning.model.PersonRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test

@DataJpaTest(showSql = true)
@ExtendWith(PostgreSQLDatabaseExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MyTest {
    @Autowired
    private lateinit var peopleRepository: PersonRepository

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    fun test() {
        val people = List(100) { peopleRepository.saveAndFlush(Person(name = "Person $it")) }
        groupRepository.saveAndFlush(Group(name = "Group 1", people = people.toMutableSet()))

        entityManager.clear()

        groupRepository.findAll().toHashSet()
    }
}
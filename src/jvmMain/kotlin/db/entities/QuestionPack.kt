package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object QuestionPacks : IntIdTable() {
    val name = varchar(name = "name", length = 100)
}

class QuestionPack(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuestionPack>(QuestionPacks)

    var name by QuestionPacks.name
    val questions by Question referrersOn Questions.pack
}
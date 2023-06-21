package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Questions : IntIdTable() {
    val text = text(name = "text")
    val complexity = integer(name = "complexity")
    val pack = reference(name = "pack", foreign = QuestionPacks)
}

class Question(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Question>(Questions)

    var text by Questions.text
    var complexity by Questions.complexity
    var pack by QuestionPack referencedOn Questions.pack
}
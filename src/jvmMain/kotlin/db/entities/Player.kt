package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Players : IntIdTable() {
    val name = text(name = "name")
    val game = reference(name = "game", foreign = Games, onDelete = ReferenceOption.CASCADE)
}

class Player(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Player>(Players)

    var name by Players.name
    var game by Game referencedOn Players.game
}
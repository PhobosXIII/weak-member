package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Rounds : IntIdTable() {
    val game = reference(name = "game", foreign = Games)
    val currentPlayer = reference(name = "current_player", foreign = Players)
}

class Round(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Round>(Rounds)

    var game by Game referencedOn Rounds.game
    var currentPlayer by Player referencedOn Rounds.currentPlayer
}
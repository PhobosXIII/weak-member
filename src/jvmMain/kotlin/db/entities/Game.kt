package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Games : IntIdTable() {
    val name = text(name = "name")
    val roundsCount = integer(name = "rounds_count")
    val currentRound = reference(name = "current_round", foreign = Rounds).nullable()
    val bank = integer(name = "bank").default(0)
}

class Game(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Game>(Games)

    var name by Games.name
    var roundsCount by Games.roundsCount
    val players by Player referrersOn Players.game
    var currentRound by Round optionalReferencedOn Games.currentRound
    val rounds by Round referrersOn Rounds.game
    var bank by Games.bank
}
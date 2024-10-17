package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object PlayerStats : IntIdTable() {
    val round = reference(name = "round", foreign = Rounds)
    val player = reference(name = "player", foreign = Players)
    val bank = integer(name = "bank").default(0)
    val answers = integer(name = "answers").default(0)
}

class PlayerStat(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerStat>(PlayerStats)

    var round by Round referencedOn PlayerStats.round
    var player by Player referencedOn PlayerStats.player
    var bank by PlayerStats.bank
    var answers by PlayerStats.answers
}
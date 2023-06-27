package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object PlayerBanks : IntIdTable() {
    val round = reference(name = "round", foreign = Rounds)
    val player = reference(name = "player", foreign = Players)
    val bank = integer(name = "bank")
}

class PlayerBank(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerBank>(PlayerBanks)

    var round by Round referencedOn PlayerBanks.round
    var player by Player referencedOn PlayerBanks.player
    var bank by PlayerBanks.bank
}
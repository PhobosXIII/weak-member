package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Rounds : IntIdTable() {
    val game = reference(name = "game", foreign = Games)
    val number = integer(name = "number")
    val currentPlayer = reference(name = "current_player", foreign = Players).nullable()
    val currentQuestion = reference(name = "current_question", foreign = GameQuestions).nullable()
    val bank = integer(name = "bank").default(0)
    val currentBank = integer(name = "current_bank").default(0)
    val isActive = bool(name = "is_active").default(false)
}

class Round(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Round>(Rounds)

    var game by Game referencedOn Rounds.game
    var number by Rounds.number
    var currentPlayer by Player optionalReferencedOn Rounds.currentPlayer
    var currentQuestion by GameQuestion optionalReferencedOn Rounds.currentQuestion
    var bank by Rounds.bank
    var currentBank by Rounds.currentBank
    var isActive by Rounds.isActive
}
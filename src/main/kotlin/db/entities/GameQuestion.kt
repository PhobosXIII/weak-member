package db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object GameQuestions : IntIdTable() {
    val game = reference(name = "game", foreign = Games)
    val question = reference(name = "question", foreign = Questions)
    val player = reference(name = "player", foreign = Players).nullable()
    val isCorrect = bool(name = "is_correct").default(false)
}

class GameQuestion(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GameQuestion>(GameQuestions)

    var game by Game referencedOn GameQuestions.game
    var question by Question referencedOn GameQuestions.question
    var player by Player optionalReferencedOn GameQuestions.player
    var isCorrect by GameQuestions.isCorrect
}
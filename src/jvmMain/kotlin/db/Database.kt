package db

import db.entities.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Database {

    companion object {
        fun register() {
            Database.connect(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                driver = "org.h2.Driver",
                user = "root",
                password = ""
            )
        }

        fun fillData() {
            transaction {
                SchemaUtils.create(QuestionPacks, Questions, Games, Players, Rounds, GameQuestions, PlayerBanks)

                val testPack = QuestionPack.new {
                    name = "Test pack"
                }

                Question.new {
                    text = "Some text 1"
                    complexity = 0
                    pack = testPack
                }

                Question.new {
                    text = "Some text 2"
                    complexity = 2
                    pack = testPack
                }

                Question.new {
                    text = "Some text 3"
                    complexity = 1
                    pack = testPack
                }

                val testGame = Game.new {
                    name = "Новая игра"
                    roundsCount = 6
                }

                val testPlayers = listOf(
                    Player.new {
                        this.name = "Max"
                        this.order = 0
                        this.game = testGame
                    },
                    Player.new {
                        this.name = "Alex"
                        this.order = 1
                        this.game = testGame
                    },
                    Player.new {
                        this.name = "John"
                        this.order = 2
                        this.game = testGame
                    }
                )
                val firstPlayer = testPlayers.minByOrNull { it.name }

                for (i in 1..6) {
                    val round = Round.new {
                        this.game = testGame
                        this.number = i
                        if (i == 1) this.currentPlayer = firstPlayer
                    }

                    if (i == 1) testGame.currentRound = round

                    testPlayers.forEach { player ->
                        PlayerBank.new {
                            this.player = player
                            this.round = round
                        }
                    }
                }

                testPack.questions.forEach { question ->
                    GameQuestion.new {
                        this.game = testGame
                        this.question = question
                    }
                }
            }
        }
    }
}

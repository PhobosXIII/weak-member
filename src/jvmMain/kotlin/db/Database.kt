package db

import db.entities.Question
import db.entities.QuestionPack
import db.entities.QuestionPacks
import db.entities.Questions
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
                SchemaUtils.create(QuestionPacks, Questions)

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
            }
        }
    }
}

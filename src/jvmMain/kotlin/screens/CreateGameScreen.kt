package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.CounterField
import commonUi.SpacerWidth
import db.entities.Game
import db.entities.Player
import db.entities.Round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import navigation.createGameScreenRoute
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import viewStates.PlayerViewState

@Composable
fun CreateGameScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppBar(
                title = "Создать новую игру",
                onBackClick = { navController.navigateBack() },
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var name by remember { mutableStateOf("Новая игра") }
            var roundsCount by remember { mutableStateOf(6) }
            val players = remember { mutableStateListOf<PlayerViewState>() }
            val buttonEnabled by derivedStateOf { name.isNotEmpty() }

            TextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = "Имя игры")
                },
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Количество раундов:")
                SpacerWidth(8.dp)
                CounterField(
                    value = roundsCount,
                    onValueChange = { roundsCount = it },
                )
            }

            Players(players)

            Button(
                onClick = {
                    coroutineScope.launch {
                        newSuspendedTransaction(context = Dispatchers.IO) {
                            addLogger(StdOutSqlLogger)
                            val newGame = Game.new {
                                this.name = name
                                this.roundsCount = roundsCount
                            }

                            for (i in 1..roundsCount) {
                                val round = Round.new {
                                    this.game = newGame
                                    this.number = i
                                }

                                if (i == 1) newGame.currentRound = round
                            }

                            for (player in players) {
                                Player.new {
                                    this.name = player.name
                                    this.number = player.number
                                    this.game = newGame
                                }
                            }

                            navController.navigate(createGameScreenRoute(newGame.id.value), addToBackStack = false)
                        }
                    }
                },
                enabled = buttonEnabled,
            ) {
                Text(text = "Создать")
            }
        }
    }
}

@Composable
private fun Players(
    players: SnapshotStateList<PlayerViewState>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Игроки",
            style = MaterialTheme.typography.h4,
        )

        for ((i, player) in players.withIndex()) {
            TextField(
                value = player.name,
                onValueChange = {
                    val updatedPlayer = player.copy(name = it)
                    players[i] = updatedPlayer
                },
            )
        }

        Row {
            var name by remember { mutableStateOf("") }
            TextField(
                value = name,
                onValueChange = { name = it },
            )
            SpacerWidth(8.dp)
            Button(
                onClick = {
                    players.add(PlayerViewState(name = name))
                    name = ""
                },
            ) {
                Text(text = "Добавить")
            }
        }

    }
}

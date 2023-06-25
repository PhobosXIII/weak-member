package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

                            val firstPlayer = players.minByOrNull { it.name }?.let { player ->
                                players.removeIf { it.number == player.number }

                                Player.new {
                                    this.name = player.name
                                    this.number = player.number
                                    this.game = newGame
                                }

                            }

                            for (i in 1..roundsCount) {
                                val round = Round.new {
                                    this.game = newGame
                                    this.number = i
                                    if (i == 1) this.currentPlayer = firstPlayer
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
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Игроки",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        for (player in players) {
            val i = player.number
            Row {
                TextField(
                    value = player.name,
                    onValueChange = {
                        players[i - 1] = player.copy(name = it)
                    },
                )

                SpacerWidth(8.dp)

                if (i != players.size && players.size != 1) {
                    IconButton(
                        onClick = {
                            players[i - 1] = players[i].copy(number = player.number)
                            players[i] = player.copy(number = player.number + 1)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = null,
                        )
                    }
                }

                if (i != 1 && players.size != 1) {
                    SpacerWidth(4.dp)
                    IconButton(
                        onClick = {
                            players[i - 1] = players[i - 2].copy(number = player.number)
                            players[i - 2] = player.copy(number = player.number - 1)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                        )
                    }
                }
            }
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
                    players.add(
                        PlayerViewState(
                            name = name,
                            number = players.size + 1,
                        )
                    )
                    name = ""
                },
                enabled = name.isNotBlank()
            ) {
                Text(text = "Добавить")
            }
        }

    }
}

@file:OptIn(ExperimentalComposeUiApi::class)

package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.CounterField
import commonUi.DropdownSelector
import commonUi.SpacerWidth
import db.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import navigation.createGameScreenRoute
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import viewStates.PlayerViewState
import viewStates.QuestionPackViewState

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
        val packs = transaction {
            QuestionPack.all().toList()
        }.map { QuestionPackViewState(id = it.id.value, name = it.name) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            var name by remember { mutableStateOf("Новая игра") }
            var roundsCount by remember { mutableStateOf(6) }
            val players = remember { mutableStateListOf<PlayerViewState>() }
            var selectedPack: QuestionPackViewState? by remember { mutableStateOf(null) }
            val buttonEnabled by derivedStateOf {
                name.isNotEmpty() && selectedPack != null && players.isNotEmpty()
            }

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
                    minValue = 1,
                    maxValue = 13,
                )
            }

            DropdownSelector(
                items = packs,
                value = selectedPack,
                onSelect = { selectedPack = it },
                placeholder = "Выберите пакет вопросов",
                modifier = Modifier.width(350.dp),
            )

            Players(players)

            Button(
                onClick = {
                    coroutineScope.launch {
                        newSuspendedTransaction(context = Dispatchers.IO) {
                            val newGame = Game.new {
                                this.name = name
                                this.roundsCount = roundsCount
                            }

                            val newPlayers = players.map { player ->
                                Player.new {
                                    this.name = player.name
                                    this.order = player.order
                                    this.game = newGame
                                }
                            }

                            val firstPlayer = newPlayers.minByOrNull { it.name }

                            for (i in 1..roundsCount) {
                                val round = Round.new {
                                    this.game = newGame
                                    this.number = i
                                    if (i == 1) this.currentPlayer = firstPlayer
                                }

                                if (i == 1) newGame.currentRound = round

                                newPlayers.forEach { player ->
                                    PlayerBank.new {
                                        this.player = player
                                        this.round = round
                                    }
                                }
                            }

                            QuestionPack.findById(selectedPack?.id ?: 0)?.questions?.forEach { question ->
                                GameQuestion.new {
                                    this.game = newGame
                                    this.question = question
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
            val i = player.order
            Row {
                TextField(
                    value = player.name,
                    onValueChange = {
                        players[i - 1] = player.copy(name = it)
                    },
                    singleLine = true,
                )

                SpacerWidth(8.dp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (i != players.size && players.size != 1) {
                        IconButton(
                            onClick = {
                                players[i - 1] = players[i].copy(order = player.order)
                                players[i] = player.copy(order = player.order + 1)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDownward,
                                contentDescription = null,
                            )
                        }
                    }

                    if (i != 1 && players.size != 1) {
                        IconButton(
                            onClick = {
                                players[i - 1] = players[i - 2].copy(order = player.order)
                                players[i - 2] = player.copy(order = player.order - 1)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowUpward,
                                contentDescription = null,
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            players.remove(players[i - 1])
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color.Red,
                        )
                    }
                }
            }
        }

        Row {
            var name by remember { mutableStateOf("") }
            val onAddPlayer = {
                players.add(
                    PlayerViewState(
                        name = name,
                        order = players.size + 1,
                    )
                )
                name = ""
            }

            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                modifier = Modifier.onPreviewKeyEvent {
                    when {
                        it.key == Key.Enter && it.type == KeyEventType.KeyUp -> {
                            onAddPlayer()
                            true
                        }

                        else -> false
                    }
                },
            )

            SpacerWidth(8.dp)


            Button(
                onClick = onAddPlayer,
                enabled = name.isNotBlank()
            ) {
                Text(text = "Добавить")
            }
        }

    }
}

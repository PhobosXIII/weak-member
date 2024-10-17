package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.QuestionCard
import commonUi.SpacerHeight
import db.entities.*
import findParameterValue
import kotlinx.coroutines.launch
import navigation.NavController
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import viewStates.PlayerViewState
import java.net.URI

@Composable
fun GameScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var update: Int by remember { mutableStateOf(0) }
    var game: Game? by remember { mutableStateOf(null) }
    var currentRound: Round? by remember { mutableStateOf(null) }
    var currentPlayer: Player? by remember { mutableStateOf(null) }
    var currentPlayerStat: PlayerStat? by remember { mutableStateOf(null) }
    var players by remember { mutableStateOf(emptyList<Player>()) }
    var playersStat by remember { mutableStateOf(emptyList<PlayerStat>()) }
    var currentQuestion: Question? by remember { mutableStateOf(null) }
    var totalBank by remember { mutableStateOf(0) }

    val id = URI(navController.currentScreen.value).findParameterValue("id")?.toInt() ?: -1
    transaction {
        game = Game.findById(id)
        players = game?.players?.toList()?.sortedBy { it.order } ?: emptyList()
    }

    LaunchedEffect(key1 = update) {
        transaction {
            currentRound = game?.currentRound
            currentPlayer = currentRound?.currentPlayer
            currentQuestion = currentRound?.currentQuestion?.question
            totalBank = game?.rounds?.sumOf { it.bank } ?: 0

            currentRound?.let { round ->
                playersStat = PlayerStat.find { PlayerStats.round eq round.id }.toList()

                currentPlayer?.let { player ->
                    currentPlayerStat =
                        PlayerStat.find { (PlayerStats.player eq player.id) and (PlayerStats.round eq round.id) }
                            .firstOrNull()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = game?.name ?: "",
                onBackClick = { navController.navigateBack() },
                actions = {
                    Text(
                        text = "Общий банк: $totalBank",
                        modifier = Modifier.padding(end = 16.dp),
                    )
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = "Раунд ${currentRound?.number}",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                SpacerHeight(16.dp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Players(
                        players = players
                            .map { player ->
                                val playerStat = transaction {
                                    playersStat.find { it.player == currentPlayer }
                                }

                                PlayerViewState(
                                    name = player.name,
                                    current = currentPlayer?.order == player.order,
                                    answers = playerStat?.answers ?: 0,
                                    bank = playerStat?.bank ?: 0,
                                )
                            },
                        modifier = Modifier.weight(0.3f),
                    )

                    Box(
                        modifier = Modifier.weight(0.7f),
                    ) {
                        currentQuestion?.let {
                            QuestionCard(
                                text = it.text,
                                complexity = it.complexity,
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val isRoundActive = currentRound?.isActive == true
                Button(
                    onClick = {
                        coroutineScope.launch {
                            newSuspendedTransaction {
                                currentRound?.let { round ->
                                    if (isRoundActive) {
                                        val nextRound = game?.rounds?.firstOrNull { it.number == round.number + 1 }
                                            ?: return@newSuspendedTransaction
                                        game?.currentRound = nextRound
                                    } else {
                                        round.currentQuestion =
                                            GameQuestion.find { GameQuestions.player eq null }.firstOrNull()
                                        round.isActive = true
                                    }
                                }
                            }
                            update++
                        }
                    },
                ) {
                    val text = if (isRoundActive) "Закончить раунд" else "Начать раунд"
                    Text(text = text)
                }

                if (isRoundActive) {
                    val onClick: (Boolean) -> Unit = { isCorrect ->
                        coroutineScope.launch {
                            newSuspendedTransaction {
                                currentQuestion?.let { question ->
                                    val gameQuestion = GameQuestion.findById(question.id)
                                    gameQuestion?.player = currentPlayer
                                    gameQuestion?.isCorrect = isCorrect
                                }
                                currentRound?.let { round ->
                                    if (isCorrect) {
                                        val answers = currentPlayerStat?.answers
                                        if (answers != null) {
                                            currentPlayerStat?.answers = answers.inc()
                                        }
                                    }

                                    round.currentQuestion =
                                        GameQuestion.find { GameQuestions.player eq null }.firstOrNull()
                                    val nextPlayer = players.firstOrNull { it.order == currentPlayer?.order?.inc() }
                                        ?: players.firstOrNull()
                                    round.currentPlayer = nextPlayer
                                }
                            }
                            update++
                        }
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF009900),
                            contentColor = Color.White,
                        ),
                        onClick = { onClick(true) },
                    ) {
                        Text(text = "Верно")
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red,
                            contentColor = Color.White,
                        ),
                        onClick = { onClick(false) },
                    ) {
                        Text(text = "Не верно")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                newSuspendedTransaction {
                                    currentRound?.let { round ->
                                        currentPlayerStat?.bank = round.currentBank
                                        round.bank += round.currentBank
                                        round.currentBank = 0
                                    }
                                }
                                update++
                            }
                        },
                    ) {
                        Text(text = "Банк")
                    }
                }
            }
        }
    }
}

@Composable
private fun Players(
    players: List<PlayerViewState>,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = "Игроки",
            style = MaterialTheme.typography.h5,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Имя",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.5f),
                )

                Text(
                    text = "Ответы",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.25f),
                )

                Text(
                    text = "Банк",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.25f),
                )
            }

            for (player in players) {
                val fontWeight = if (player.current) FontWeight.Bold else FontWeight.Normal

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    Card(
                        modifier = Modifier.weight(0.5f),
                        border = if (player.current) BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colors.secondary
                        ) else null,
                    ) {
                        Text(
                            text = player.name,
                            fontWeight = fontWeight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp),
                        )
                    }

                    Text(
                        text = player.answers.toString(),
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp).weight(0.25f),
                    )

                    Text(
                        text = player.bank.toString(),
                        fontWeight = fontWeight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(6.dp).weight(0.25f),
                    )
                }
            }
        }
    }
}
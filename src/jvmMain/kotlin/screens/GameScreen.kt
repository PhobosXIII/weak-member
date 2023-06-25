package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import db.entities.Game
import db.entities.Player
import db.entities.Round
import findParameterValue
import kotlinx.coroutines.launch
import navigation.NavController
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import viewStates.PlayerViewState
import java.net.URI

@Composable
fun GameScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var game: Game? by remember { mutableStateOf(null) }
    var currentRound: Round? by remember { mutableStateOf(null) }
    var currentPlayer: Player? by remember { mutableStateOf(null) }
    var players by remember { mutableStateOf(emptyList<Player>()) }

    val id = URI(navController.currentScreen.value).findParameterValue("id")?.toInt() ?: -1
    transaction {
        game = Game.findById(id)
        currentRound = game?.currentRound
        currentPlayer = currentRound?.currentPlayer
        players = game?.players?.toList() ?: emptyList()
    }

    Scaffold(
        topBar = {
            AppBar(
                title = game?.name ?: "",
                onBackClick = { navController.navigateBack() },
                actions = {
                    Text(
                        text = "Общий банк: ${game?.bank}",
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
            Text(
                text = "Раунд ${currentRound?.number}",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Players(
                players = players
                    .sortedBy { it.number }
                    .map {
                        PlayerViewState(
                            name = it.name,
                            current = currentPlayer == it
                        )
                    }
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        newSuspendedTransaction {
                            currentRound?.let { round ->
                                val nextRound = game?.rounds?.firstOrNull { it.number == round.number + 1 }
                                    ?: return@newSuspendedTransaction
                                game?.currentRound = nextRound
                            }

                        }
                    }
                },
            ) {
                Text("Закончить раунд")
            }
        }
    }
}

@Composable
private fun Players(
    players: List<PlayerViewState>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Игроки",
            style = MaterialTheme.typography.h5,
        )

        for (player in players) {
            Card(
                border = if (player.current) BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colors.secondary
                ) else null,
            ) {
                Text(
                    text = player.name,
                    fontWeight = if (player.current) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(6.dp),
                )
            }
        }
    }
}
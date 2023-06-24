package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.CounterField
import db.entities.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import navigation.createGameScreenRoute
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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
            val buttonEnabled by derivedStateOf { name.isNotEmpty() }

            TextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = "Имя игры")
                },
            )

            CounterField(
                value = roundsCount,
                onValueChange = { roundsCount = it },
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val newGame = newSuspendedTransaction(context = Dispatchers.IO) {
                            Game.new {
                                this.name = name
                                this.roundsCount = roundsCount
                            }
                        }
                        navController.navigate(createGameScreenRoute(newGame.id.value), addToBackStack = false)
                    }
                },
                enabled = buttonEnabled,
            ) {
                Text(text = "Создать")
            }
        }
    }
}

package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import db.entities.Game
import navigation.NavController
import navigation.Screen
import navigation.createGameScreenRoute
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun GamesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Игры",
                onBackClick = { navController.navigateBack() },
            )
        },
    ) {
        val games = transaction {
            Game.all().toList()
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateGameScreen.name) },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.align(Alignment.BottomEnd),
            )

            if (games.isEmpty()) {
                Text(
                    text = "Вы пока не создали ни одной игры.",
                )
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(games) { game ->
                        Button(onClick = {
                            navController.navigate(createGameScreenRoute(game.id.value))
                        }) {
                            Text(text = game.name)
                        }
                    }
                }
            }
        }
    }
}
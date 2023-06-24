package screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import commonUi.AppBar
import db.entities.Game
import findParameterValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.net.URI

@Composable
fun GameScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var game: Game? by remember { mutableStateOf(null) }

    coroutineScope.launch {
        val id = URI(navController.currentScreen.value).findParameterValue("id")?.toInt() ?: -1
        game = newSuspendedTransaction(Dispatchers.IO) {
            Game.findById(id)
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = game?.name ?: "",
                onBackClick = { navController.navigateBack() },
            )
        },
    ) {

    }
}
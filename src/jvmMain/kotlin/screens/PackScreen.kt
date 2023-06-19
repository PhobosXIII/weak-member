package screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.PackDialog
import db.entities.QuestionPack
import findParameterValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.net.URI

@Composable
fun PackScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var pack: QuestionPack? by remember { mutableStateOf(null) }

    coroutineScope.launch {
        val id = URI(navController.currentScreen.value).findParameterValue("id")?.toInt() ?: -1
        pack = newSuspendedTransaction(Dispatchers.IO) {
            QuestionPack.findById(id)
        }
    }

    var isDialogOpen by remember { mutableStateOf(false) }
    if (isDialogOpen) {
        PackDialog(
            onCloseRequest = { isDialogOpen = false },
            onSave = { packName ->
                isDialogOpen = false
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        pack?.name = packName
                    }
                }
            },
            packName = pack?.name ?: "",
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = pack?.name ?: "",
                onBackClick = { navController.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { isDialogOpen = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {

            }
        }
    )
}

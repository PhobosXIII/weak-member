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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.PackDialog
import db.entities.QuestionPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import navigation.createPackScreenRoute
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Composable
fun PacksScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var isDialogOpen by remember { mutableStateOf(false) }
    if (isDialogOpen) {
        PackDialog(
            onCloseRequest = { isDialogOpen = false },
            onSave = { packName ->
                isDialogOpen = false
                coroutineScope.launch {
                    val newPack = newSuspendedTransaction(Dispatchers.IO) {
                        QuestionPack.new {
                            name = packName
                        }
                    }
                    navController.navigate(createPackScreenRoute(newPack.id.value))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Пакеты вопросов",
                onBackClick = { navController.navigateBack() },
            )
        },
    ) {
        val packs = transaction {
            QuestionPack.all().toList()
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (packs.isEmpty()) {
                Text(
                    text = "Вы пока не добавили ни одного пакета вопрпосов.",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(packs) { pack ->
                        Button(onClick = {
                            navController.navigate(createPackScreenRoute(pack.id.value))
                        }) {
                            Text(text = pack.name)
                        }
                    }
                }
            }

            AddPackButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onImport = {},
                onCreate = {
                    isDialogOpen = true
                }
            )
        }
    }
}

@Composable
private fun AddPackButton(
    modifier: Modifier = Modifier,
    onImport: () -> Unit,
    onCreate: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
    ) {
        FloatingActionButton(
            onClick = { expanded = true },
            content = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onImport()
                    expanded = false
                }
            ) {
                Text("Импорт csv")
            }
            DropdownMenuItem(
                onClick = {
                    onCreate()
                    expanded = false
                }
            ) {
                Text("Создать")
            }
        }
    }
}

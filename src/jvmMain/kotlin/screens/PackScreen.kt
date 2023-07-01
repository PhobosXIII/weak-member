package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import commonUi.*
import db.entities.Question
import db.entities.QuestionPack
import findParameterValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import navigation.NavController
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import viewStates.QuestionViewState
import java.net.URI

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PackScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var pack: QuestionPack? by remember { mutableStateOf(null) }
    var currentQuestion: QuestionViewState by remember { mutableStateOf(QuestionViewState()) }
    val packName = pack?.name ?: ""

    coroutineScope.launch {
        val id = URI(navController.currentScreen.value).findParameterValue("id")?.toInt() ?: -1
        pack = newSuspendedTransaction(Dispatchers.IO) {
            QuestionPack.findById(id)
        }
    }

    var isPackDialogOpen by remember { mutableStateOf(false) }
    if (isPackDialogOpen) {
        PackDialog(
            onCloseRequest = { isPackDialogOpen = false },
            onSave = { newPackName ->
                isPackDialogOpen = false
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        pack?.name = newPackName
                    }
                }
            },
            packName = pack?.name ?: "",
        )
    }

    var isQuestionDialogOpen by remember { mutableStateOf(false) }
    if (isQuestionDialogOpen) {
        QuestionDialog(
            onCloseRequest = { isQuestionDialogOpen = false },
            onSave = { question ->
                isQuestionDialogOpen = false
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        if (question.isNew()) {
                            Question.new {
                                text = question.text
                                complexity = question.complexity
                                this.pack = QuestionPack[question.packId]
                            }
                        } else {
                            val q = Question.findById(question.id)
                            q?.text = question.text
                            q?.complexity = question.complexity
                        }
                    }
                }
            },
            question = currentQuestion.copy(packId = pack?.id?.value ?: -1)
        )
    }

    var isPackDeleteConfirmationDialogOpen by remember { mutableStateOf(false) }
    if (isPackDeleteConfirmationDialogOpen) {
        DeleteConfirmationDialog(
            onCloseRequest = { isPackDeleteConfirmationDialogOpen = false },
            onConfirm = {
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        pack?.delete()
                        isPackDeleteConfirmationDialogOpen = false
                        navController.navigateBack()
                    }
                }
            },
            text = "Вы уверены, что хотите удалить пакет '$packName' со всеми вопросами?",
            title = "Удалить пакет?",
        )
    }

    var isQuestionDeleteConfirmationDialogOpen by remember { mutableStateOf(false) }
    if (isQuestionDeleteConfirmationDialogOpen) {
        DeleteConfirmationDialog(
            onCloseRequest = { isQuestionDeleteConfirmationDialogOpen = false },
            onConfirm = {
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        Question.findById(currentQuestion.id)?.delete()
                        isQuestionDeleteConfirmationDialogOpen = false
                    }
                }
            },
            text = "Вы уверены, что хотите удалить вопрос?",
            title = "Удалить вопрос?",
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = packName,
                onBackClick = { navController.navigateBack() },
                actions = {
                    IconButton(
                        onClick = { isPackDialogOpen = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                        )
                    }

                    IconButton(
                        onClick = { isQuestionDeleteConfirmationDialogOpen = true },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) {
        val questions = transaction {
            pack?.questions?.toList() ?: emptyList()
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (questions.isEmpty()) {
                Text(
                    text = "Вы пока не добавили ни одного вопроса.",
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(questions) { question ->
                        Box {
                            var expanded by remember { mutableStateOf(false) }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        isQuestionDialogOpen = true
                                        expanded = false
                                    }
                                ) {
                                    Text("Редактировать")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        isQuestionDeleteConfirmationDialogOpen = true
                                        expanded = false
                                    }
                                ) {
                                    Text("Удалить")
                                }
                            }

                            QuestionCard(
                                text = question.text,
                                complexity = question.complexity,
                                onClick = {
                                    currentQuestion = QuestionViewState(
                                        id = question.id.value,
                                        text = question.text,
                                        complexity = question.complexity,
                                    )
                                    expanded = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    currentQuestion = QuestionViewState()
                    isQuestionDialogOpen = true
                },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import commonUi.AppBar
import commonUi.PackDialog
import commonUi.QuestionDialog
import commonUi.getComplexityColor
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
            onSave = { packName ->
                isPackDialogOpen = false
                coroutineScope.launch {
                    newSuspendedTransaction(Dispatchers.IO) {
                        pack?.name = packName
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

    Scaffold(
        topBar = {
            AppBar(
                title = pack?.name ?: "",
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
                }
            )
        },
        content = {
            val questions = transaction {
                pack?.questions?.toList() ?: emptyList()
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
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

                if (questions.isEmpty()) {
                    Text(
                        text = "Вы не добавили пока ни одного вопроса.",
                    )
                } else {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(questions) { question ->
                            Card(
                                border = BorderStroke(width = 2.dp, color = question.complexity.getComplexityColor()),
                                onClick = {
                                    currentQuestion = QuestionViewState(
                                        id = question.id.value,
                                        text = question.text,
                                        complexity = question.complexity,
                                    )
                                    isQuestionDialogOpen = true
                                },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = question.text,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                            .align(Alignment.Center),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

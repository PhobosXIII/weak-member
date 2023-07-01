package commonUi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import getComplexityColor
import viewStates.QuestionViewState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionDialog(
    onCloseRequest: () -> Unit,
    onSave: (QuestionViewState) -> Unit,
    question: QuestionViewState,
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        title = if (question.isNew()) "Создать новый вопрос" else "Редактировать вопрос",
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var text by remember { mutableStateOf(question.text) }
            var complexity by remember { mutableStateOf(question.complexity) }
            val textFieldFocusRequester = remember { FocusRequester() }

            LaunchedEffect(key1 = textFieldFocusRequester) {
                textFieldFocusRequester.requestFocus()
            }

            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier.fillMaxWidth().focusRequester(textFieldFocusRequester),
                minLines = 3,
                label = {
                    Text(text = "Текст вопроса")
                },
            )

            SpacerHeight(16.dp)

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (i in 0..2) {
                    Chip(
                        onClick = { complexity = i },
                        border = if (complexity == i) BorderStroke(
                            width = 2.dp,
                            color = MaterialTheme.colors.primary,
                        ) else null,
                        colors = ChipDefaults.chipColors(
                            backgroundColor = i.getComplexityColor(),
                            contentColor = Color.Black,
                        )
                    ) {
                        Text(
                            text = i.getComplexityLabel()
                        )
                    }
                }
            }

            SpacerHeight(16.dp)

            Button(
                onClick = { onSave(question.copy(text = text, complexity = complexity)) },
                enabled = text.isNotBlank()
            ) {
                Text(
                    text = if (question.isNew()) "Создать" else "Изменить",
                )
            }
        }
    }
}

private fun Int.getComplexityLabel(): String = when (this) {
    0 -> "Легкий"
    1 -> "Средний"
    2 -> "Сложный"
    else -> "Запредельный"
}

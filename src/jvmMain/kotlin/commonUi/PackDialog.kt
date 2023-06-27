package commonUi

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PackDialog(
    onCloseRequest: () -> Unit,
    onSave: (String) -> Unit,
    packName: String = "",
) {
    val maxNameLength = 100
    Dialog(
        onCloseRequest = onCloseRequest,
        title = if (packName.isEmpty()) "Создать новый пакет" else "Редактировать пакет",
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var name by remember { mutableStateOf(packName) }
            val textFieldFocusRequester = remember { FocusRequester() }

            LaunchedEffect(key1 = textFieldFocusRequester) {
                textFieldFocusRequester.requestFocus()
            }

            TextField(
                value = name,
                onValueChange = {
                    if (it.length <= maxNameLength) {
                        name = it
                    }
                },
                label = {
                    Text(text = "Имя пакета")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(textFieldFocusRequester)
                    .onPreviewKeyEvent {
                        when {
                            it.key == Key.Enter && it.type == KeyEventType.KeyUp -> {
                                onSave(name)
                                true
                            }

                            else -> false
                        }
                    },
                singleLine = true,
            )
            SpacerHeight(4.dp)
            Text(
                text = "${name.length}/$maxNameLength",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.align(Alignment.End),
            )

            SpacerHeight(16.dp)

            Button(
                onClick = { onSave(name) },
                enabled = name.isNotBlank()
            ) {
                Text(text = if (packName.isEmpty()) "Создать" else "Изменить")
            }
        }
    }
}
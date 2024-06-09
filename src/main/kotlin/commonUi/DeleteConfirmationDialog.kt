package commonUi

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow

@Composable
fun DeleteConfirmationDialog(
    onCloseRequest: () -> Unit,
    onConfirm: () -> Unit,
    text: String,
    title: String,
) {
    DialogWindow(
        onCloseRequest = onCloseRequest,
        title = title,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = text)

            SpacerHeight(16.dp)

            Row {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White,
                    )
                ) {
                    Text(text = "Удалить")
                }

                SpacerWidth(8.dp)

                Button(
                    onClick = onCloseRequest,
                ) {
                    Text(text = "Отмена")
                }
            }
        }
    }
}
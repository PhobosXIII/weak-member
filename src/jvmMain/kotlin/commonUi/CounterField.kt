package commonUi

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CounterField(
    value: Int = 0,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 99,
) {
    var newValue by remember { mutableStateOf(value) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { if (newValue != minValue) newValue -= 1 },
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                contentDescription = null,
            )
        }

        TextField(
            value = newValue.toString(),
            onValueChange = { onValueChange(newValue) },
            modifier = Modifier.width(60.dp),
            readOnly = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        )

        IconButton(
            onClick = { if (newValue != maxValue) newValue += 1 },
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun CounterFieldPreview() {
    MaterialTheme {
        CounterField(
            value = 3,
            onValueChange = {},
        )
    }
}
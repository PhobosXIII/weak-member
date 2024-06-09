package commonUi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    value: T?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(4.dp)

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .defaultMinSize(minWidth = 280.dp, minHeight = 40.dp)
            .clip(shape = shape)
            .border(BorderStroke(width = 1.dp, color = Color.LightGray), shape = shape)
            .clickable { expanded = !expanded },
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value?.toString() ?: placeholder,
                style = if (value == null) MaterialTheme.typography.caption else TextStyle.Default,
                modifier = Modifier.weight(1f),
            )
            SpacerWidth(4.dp)
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelect(item)
                    }
                ) {
                    Text(
                        text = item.toString(),
                    )
                }
            }
        }
    }
}
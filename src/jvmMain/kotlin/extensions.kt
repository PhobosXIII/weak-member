import androidx.compose.ui.graphics.Color
import java.net.URI

fun URI.findParameterValue(parameterName: String): String? {
    return rawQuery?.split('&')?.map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }?.firstOrNull { it.first == parameterName }?.second
}

fun Int.getComplexityColor(): Color = when (this) {
    0 -> Color.Green
    1 -> Color.Yellow
    2 -> Color.Red
    else -> Color.Gray
}

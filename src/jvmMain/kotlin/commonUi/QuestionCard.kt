package commonUi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import getComplexityColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionCard(
    text: String,
    complexity: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        border = BorderStroke(width = 2.dp, color = complexity.getComplexityColor()),
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}
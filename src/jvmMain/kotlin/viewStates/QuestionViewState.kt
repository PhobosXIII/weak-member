package viewStates

import androidx.compose.runtime.Immutable

@Immutable
data class QuestionViewState(
    val id: Int = -1,
    val text: String = "",
    val complexity: Int = 0,
    val packId: Int = -1,
) {
    fun isNew() = id == -1
}

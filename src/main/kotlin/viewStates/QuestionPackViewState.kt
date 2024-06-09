package viewStates

import androidx.compose.runtime.Immutable

@Immutable
data class QuestionPackViewState(
    val id: Int = -1,
    val name: String = "",
) {
    override fun toString(): String {
        return name
    }
}
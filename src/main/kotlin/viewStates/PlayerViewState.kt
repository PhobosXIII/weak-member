package viewStates

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerViewState(
    val id: Int = -1,
    val name: String = "",
    val order: Int = 0,
    val current: Boolean = false,
    val answers: Int = 0,
    val bank: Int = 0,
)
package viewStates

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerViewState(
    val id: Int = -1,
    val name: String = "",
    val number: Int = 0,
    val current: Boolean = false,
)
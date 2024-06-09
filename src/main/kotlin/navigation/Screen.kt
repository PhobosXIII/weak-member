package navigation

enum class Screen {
    MainScreen,
    PacksScreen,
    PackScreen,
    GamesScreen,
    GameScreen,
    CreateGameScreen,
}

fun createPackScreenRoute(packId: Int): String {
    return "${Screen.PackScreen.name}?id=$packId"
}

fun createGameScreenRoute(gameId: Int): String {
    return "${Screen.GameScreen.name}?id=$gameId"
}

package navigation

enum class Screen {
    MainScreen,
    PacksScreen,
    PackScreen,
}

fun createPackScreenRoute(packId: Int): String {
    return "${Screen.PackScreen.name}?id=$packId"
}

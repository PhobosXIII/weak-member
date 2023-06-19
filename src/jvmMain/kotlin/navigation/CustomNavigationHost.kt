package navigation

import androidx.compose.runtime.Composable
import screens.MainScreen
import screens.PackScreen
import screens.PacksScreen

@Composable
fun CustomNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.MainScreen.name) {
            MainScreen(navController)
        }

        composable(Screen.PacksScreen.name) {
            PacksScreen(navController)
        }

        composable(Screen.PackScreen.name) {
            PackScreen(navController)
        }
    }.build()
}

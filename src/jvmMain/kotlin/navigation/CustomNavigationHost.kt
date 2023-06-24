package navigation

import androidx.compose.runtime.Composable
import screens.*

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

        composable(Screen.GamesScreen.name) {
            GamesScreen(navController)
        }

        composable(Screen.GameScreen.name) {
            GameScreen(navController)
        }

        composable(Screen.CreateGameScreen.name) {
            CreateGameScreen(navController)
        }
    }.build()
}

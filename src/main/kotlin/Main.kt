import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import db.Database
import navigation.CustomNavigationHost
import navigation.Screen
import navigation.rememberNavController


fun main() = application {
    Database.register()
    Database.fillData()

    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource("icon.ico"),
        title = "Слабое звено",
    ) {
        App()
    }
}

@Composable
@Preview
fun App() {
    val navController by rememberNavController(Screen.MainScreen.name)

    MaterialTheme {
        Surface(
            modifier = Modifier.background(color = MaterialTheme.colors.background)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CustomNavigationHost(navController = navController)
            }
        }
    }
}

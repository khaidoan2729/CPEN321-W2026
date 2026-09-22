package com.example.cpen321application.navigation
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cpen321application.ui.screens.HomeScreen
import com.example.cpen321application.ui.screens.SignInScreen
import com.example.cpen321application.ui.screens.DashboardScreen
import com.example.cpen321application.ui.screens.PixelSocketScreen
import com.example.cpen321application.ui.session.SessionViewModel
import com.example.cpen321application.ui.screens.NoJobScreen
import com.example.cpen321application.ui.screens.TimerScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onSignInClick = { navController.navigate("signin") },
                onPixelSocketClick = { navController.navigate("pixelsocket") },
                onSurpriseTimerClick = { navController.navigate("timer") },
            )
        }
        composable("signin") {
            SignInScreen(onSignInSuccess = { accessToken, givenName, familyName ->
                sessionViewModel.setSession(accessToken, givenName, familyName)
                navController.navigate("dashboard")
            })
        }
        composable("dashboard") {
            DashboardScreen(
                accessToken = sessionViewModel.accessToken,
                clientFirstName = sessionViewModel.givenName,
                clientLastName = sessionViewModel.familyName,
            )
        }
        composable("pixelsocket") {
            PixelSocketScreen()
        }
        composable("timer") {
            TimerScreen(onTimerFinished = { navController.navigate("nojob") })
        }
        composable("nojob") {
            NoJobScreen()
        }
    }
}
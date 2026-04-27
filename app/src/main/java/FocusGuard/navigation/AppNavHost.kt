package FocusGuard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import FocusGuard.ui.theme.screens.splash.SplashScreen
import FocusGuard.ui.theme.screens.login.LoginScreen
import FocusGuard.ui.theme.screens.register.RegisterScreen
import FocusGuard.ui.theme.screens.dashboard.DashboardScreen
import FocusGuard.ui.theme.screens.goals.GoalsScreen
import FocusGuard.ui.theme.screens.tracking.TrackingScreen
import FocusGuard.ui.theme.screens.analytics.AnalyticsScreen
import FocusGuard.ui.theme.screens.partner.PartnerScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(Routes.GOALS) {
            GoalsScreen(navController)
        }

        composable(Routes.TRACKING) {
            TrackingScreen(navController)
        }

        composable(Routes.ANALYTICS) {
            AnalyticsScreen(navController)
        }

        composable(Routes.PARTNER) {
            PartnerScreen(navController)
        }
    }
}


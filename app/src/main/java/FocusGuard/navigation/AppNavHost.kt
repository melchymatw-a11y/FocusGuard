package FocusGuard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import FocusGuard.ui.theme.screens.splash.SplashScreen
import FocusGuard.ui.theme.screens.login.LoginScreen
import FocusGuard.ui.theme.screens.register.RegisterScreen
import FocusGuard.ui.theme.screens.dashboard.DashboardScreen
import FocusGuard.ui.theme.screens.goals.AddGoalScreen
import FocusGuard.ui.theme.screens.goals.ViewGoalsScreen
import FocusGuard.ui.theme.screens.goals.UpdateGoalScreen
import FocusGuard.ui.theme.screens.tracking.TrackingScreen
import FocusGuard.ui.theme.screens.analytics.AnalyticsScreen
import FocusGuard.ui.theme.screens.partner.PartnerScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(navController, startDestination = startDestination) {

        composable(ROUTE_SPLASH) { SplashScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_DASHBOARD) { DashboardScreen(navController) }
        composable(ROUTE_ADD_GOAL) { AddGoalScreen(navController) }
        composable(ROUTE_VIEW_GOALS) { ViewGoalsScreen(navController) }
        composable(ROUTE_TRACKING) { TrackingScreen(navController) }
        composable(ROUTE_ANALYTICS) { AnalyticsScreen(navController) }
        composable(ROUTE_PARTNER) { PartnerScreen(navController) }

        // REMOVED the simple ROUTE_UPDATE_GOAL line that was here

        composable(
            ROUTE_UPDATE_GOAL,
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId")!!
            UpdateGoalScreen(navController, goalId)
        }
    }
}
package com.tommy.digitalbankkyc.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tommy.digitalbankkyc.presentation.screens.accounts.AccountsRoute
import com.tommy.digitalbankkyc.presentation.screens.camera.CameraRoute
import com.tommy.digitalbankkyc.presentation.screens.details.CustomerDetailsRoute

@Composable
fun AppNavGraph(
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppDestinations.Accounts.route
    ) {
        composable(AppDestinations.Accounts.route) {
            AccountsRoute(
                darkTheme = darkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                onCustomerSelected = { customerId ->
                    navController.navigate(AppDestinations.Details.createRoute(customerId))
                }
            )
        }

        composable(
            route = AppDestinations.Details.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) {
            CustomerDetailsRoute(
                darkTheme = darkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                onStartKyc = { customerId ->
                    navController.navigate(AppDestinations.Camera.createRoute(customerId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestinations.Camera.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = checkNotNull(backStackEntry.arguments?.getInt("customerId"))
            CameraRoute(
                customerId = customerId,
                onCompleted = { navController.popBackStack() }
            )
        }
    }
}

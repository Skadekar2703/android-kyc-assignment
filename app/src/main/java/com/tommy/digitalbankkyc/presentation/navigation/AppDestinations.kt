package com.tommy.digitalbankkyc.presentation.navigation

sealed class AppDestinations(val route: String) {
    data object Accounts : AppDestinations("accounts")
    data object Details : AppDestinations("details/{customerId}") {
        fun createRoute(customerId: Int) = "details/$customerId"
    }

    data object Camera : AppDestinations("camera/{customerId}") {
        fun createRoute(customerId: Int) = "camera/$customerId"
    }
}

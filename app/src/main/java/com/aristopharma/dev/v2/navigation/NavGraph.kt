package com.aristopharma.dev.v2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aristopharma.dev.v2.features.dashboard.presentation.screens.DashboardScreen

import com.aristopharma.dev.v2.features.splash.presentation.screens.SplashScreen
import com.aristopharma.dev.v2.features.login.presentation.screens.SignInScreen
import com.aristopharma.dev.v2.features.login.presentation.screens.OtpScreen


@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {

    CompositionLocalProvider {
        NavHost(
            navController = navController,
            startDestination = SplashScreenNav,
//            startDestination = PrescriptionSurveyScreenNav

        ) {
            composable<SplashScreenNav> {
                SplashScreen(navController = navController)
            }

            composable<SignInScreenNav> {
                SignInScreen(
                    navController = navController,
                    onNavigateToDashboard = {
                        navController.navigate(DashboardScreenNav) {
                            popUpTo(SignInScreenNav) { inclusive = true }
                        }
                    },
                    navigateToOtp = { username, password ->
                        navController.navigate(OtpScreenNav(username, password))
                    }
                )
            }

            composable<OtpScreenNav> { 
                val args = it.toRoute<OtpScreenNav>()
                OtpScreen(
                    onLoginSuccess = {
                        navController.navigate(DashboardScreenNav) {
                            popUpTo(SignInScreenNav) { inclusive = true }
                        }
                    },
                    username = args.username,
                    password = args.password,
                    navController = navController
                ) 
            }

            composable<DashboardScreenNav> { DashboardScreen(navController) }

        }

    }
}

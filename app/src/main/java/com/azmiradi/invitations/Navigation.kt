package com.azmiradi.invitations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.azmiradi.invitations.NavigationDestination.ADD_CLASSES
import com.azmiradi.invitations.NavigationDestination.ADD_ZONE
import com.azmiradi.invitations.NavigationDestination.ALL_APPLICATIONS
import com.azmiradi.invitations.NavigationDestination.ALL_ATTENDED
import com.azmiradi.invitations.NavigationDestination.APPLICATION_DETAILS
import com.azmiradi.invitations.NavigationDestination.LOGIN
import com.azmiradi.invitations.NavigationDestination.MAIN
import com.azmiradi.invitations.all_applications.AllApplicationsScreen
import com.azmiradi.invitations.application_details.ApplicationDetailsScreen
import com.azmiradi.invitations.attendence.AttendedScreen
import com.azmiradi.invitations.dialogs.AddClassDialog
import com.azmiradi.invitations.dialogs.AddZoneDialog
import com.azmiradi.invitations.login.LoginScreen
import com.azmiradi.invitations.main_screen.MainScreen
import com.azmiradi.invitations.splah.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen {
                navController.navigate(it) {
                    popUpTo("splash_screen") {
                        inclusive = true
                    }
                }
            }
        }

        composable(LOGIN) {
            LoginScreen(onNavigate = {
                navController.navigate(it) {
                    popUpTo(LOGIN) {
                        inclusive = true
                    }
                }
            })
        }
        composable(MAIN) {
            MainScreen(onNavigate = {
                navController.navigate(it)
            })
        }

        composable(ALL_APPLICATIONS) {
            AllApplicationsScreen(onNavigate = { destination, nationalID ->
                navController.navigate(destination.replace("{applicationID}", nationalID))
            })
        }

        composable(APPLICATION_DETAILS) {
            val applicationID = it.arguments?.getString("applicationID", "not_found") ?: "not_found"
            ApplicationDetailsScreen(applicationID,
                onNavigation = {
                navController.navigate(it)
            })
        }

        dialog(ADD_CLASSES) {
            AddClassDialog {
                navController.popBackStack()
            }
        }
        dialog(ADD_ZONE) {
            AddZoneDialog {
                navController.popBackStack()
            }
        }
        composable(ALL_ATTENDED) {
            AttendedScreen(onNavigate = { destination, nationalID ->
                navController.navigate(destination.replace("{applicationID}", nationalID))
            })
        }
    }
}

object NavigationDestination {
    const val ALL_APPLICATIONS = "all_applications"
    const val SCAN_ID = "scan_id"
    const val REPORTS = "reports"
    const val APPLICATION_DETAILS = "application_details/applicationID={applicationID}"
    const val MAIN = "main"
    const val ADD_CLASSES = "add_classes"
    const val ADD_ZONE = "add_zone"
    const val ALL_ATTENDED = "all_attended"
    const val LOGIN = "LOGIN"
}


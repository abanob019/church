package com.azmiradi.easter

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.azmiradi.easter.NavigationDestination.ADD_CLASSES
import com.azmiradi.easter.NavigationDestination.ADD_ZONE
import com.azmiradi.easter.NavigationDestination.ALL_APPLICATIONS
import com.azmiradi.easter.NavigationDestination.ALL_ATTENDED
import com.azmiradi.easter.NavigationDestination.APPLICATION_DETAILS
import com.azmiradi.easter.NavigationDestination.MAIN
import com.azmiradi.easter.all_applications.AllApplicationsScreen
import com.azmiradi.easter.application_details.ApplicationDetailsScreen
import com.azmiradi.easter.attendence.AttendedScreen
import com.azmiradi.easter.dialogs.AddClassDialog
import com.azmiradi.easter.dialogs.AddZoneDialog
import com.azmiradi.easter.main_screen.MainScreen

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
            ApplicationDetailsScreen(applicationID, onNavigation = {
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


}


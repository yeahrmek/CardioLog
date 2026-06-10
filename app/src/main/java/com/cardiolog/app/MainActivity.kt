package com.cardiolog.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cardiolog.app.ui.add.AddMeasurementScreen
import com.cardiolog.app.ui.charts.ChartsScreen
import com.cardiolog.app.ui.list.MeasurementListScreen
import com.cardiolog.app.ui.profile.ProfileScreen
import com.cardiolog.app.ui.theme.CardioLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CardioLogTheme { CardioLogApp() } }
    }
}

private sealed class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Add : Destination("add?measurementId={measurementId}", "Добавить", Icons.Default.AddCircle) {
        fun createRoute(measurementId: Long = -1L) = "add?measurementId=$measurementId"
    }
    data object Charts : Destination("charts", "Графики", Icons.Default.Insights)
    data object List : Destination("list", "Список", Icons.AutoMirrored.Filled.ListAlt)
    data object Profile : Destination("profile", "Профиль", Icons.Default.Person)
}

@Composable
fun CardioLogApp() {
    val navController = rememberNavController()
    val topLevelDestinations = listOf(Destination.Add, Destination.Charts, Destination.List, Destination.Profile)
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true ||
                        (destination == Destination.Add && currentDestination?.route?.startsWith("add") == true)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            val route = if (destination == Destination.Add) Destination.Add.createRoute() else destination.route
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = destination != Destination.Add
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Add.createRoute(),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(
                route = Destination.Add.route,
                arguments = listOf(navArgument("measurementId") { type = NavType.LongType; defaultValue = -1L }),
            ) {
                AddMeasurementScreen(onSaved = {
                    navController.navigate(Destination.List.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                    }
                })
            }
            composable(Destination.Charts.route) { ChartsScreen() }
            composable(Destination.List.route) { MeasurementListScreen(onEdit = { navController.navigate(Destination.Add.createRoute(it)) }) }
            composable(Destination.Profile.route) { ProfileScreen() }
        }
    }
}

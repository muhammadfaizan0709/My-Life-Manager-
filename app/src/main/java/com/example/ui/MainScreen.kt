package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    object Tasks : Screen("tasks", "Tasks", Icons.Filled.CheckCircle)
    object Health : Screen("health", "Health", Icons.Filled.Favorite)
    object Tools : Screen("tools", "More", Icons.Filled.GridView)
    
    // Sub-screens (Not in bottom bar)
    object Notes : Screen("notes", "Notes", Icons.Filled.Notes)
    object Habits : Screen("habits", "Habits", Icons.Filled.Repeat)
    object Water : Screen("water", "Water", Icons.Filled.LocalDrink)
    object Weight : Screen("weight", "BMI", Icons.Filled.MonitorWeight)
    object Stopwatch : Screen("stopwatch", "Watch", Icons.Filled.Timer)
    object Flashlight : Screen("flashlight", "Light", Icons.Filled.Highlight)
    object Quotes : Screen("quotes", "Quotes", Icons.Filled.FormatQuote)
    object Journal : Screen("journal", "Journal", Icons.Filled.Book)
    object Goals : Screen("goals", "Goals", Icons.Filled.Flag)
    object Mood : Screen("mood", "Mood", Icons.Filled.SentimentSatisfied)
    object Sleep : Screen("sleep", "Sleep", Icons.Filled.NightsStay)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    object Vault : Screen("vault", "Vault", Icons.Filled.Lock)
    object Alarm : Screen("alarm", "Alarm", Icons.Filled.Alarm)
    object WorldClock : Screen("world_clock", "World Clock", Icons.Filled.Public)
    object Analytics : Screen("analytics", "Analytics", Icons.Filled.Insights)
    object Pedometer : Screen("pedometer", "Pedometer", Icons.Filled.DirectionsWalk)
}

val bottomBarItems = listOf(
    Screen.Dashboard,
    Screen.Tasks,
    Screen.Health,
    Screen.Tools
)

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomBarItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(Screen.Tasks.route) { TasksScreen(viewModel) }
            composable(Screen.Health.route) { HealthScreen { route -> navController.navigate(route) } }
            composable(Screen.Tools.route) { ToolsScreen { route -> navController.navigate(route) } }
            
            // Sub-screens
            composable(Screen.Notes.route) { NotesScreen(viewModel) }
            composable(Screen.Habits.route) { HabitsScreen(viewModel) }
            composable(Screen.Water.route) { WaterScreen(viewModel) }
            composable(Screen.Weight.route) { WeightBmiScreen(viewModel) }
            composable(Screen.Stopwatch.route) { StopwatchScreen() }
            composable(Screen.Flashlight.route) { FlashlightScreen() }
            composable(Screen.Quotes.route) { QuotesScreen() }
            composable(Screen.Journal.route) { JournalScreen(viewModel) }
            composable(Screen.Goals.route) { GoalsScreen(viewModel) }
            composable(Screen.Mood.route) { MoodScreen(viewModel) }
            composable(Screen.Sleep.route) { SleepScreen(viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
            composable(Screen.Vault.route) { VaultScreen(viewModel) }
            composable(Screen.Alarm.route) { AlarmScreen(viewModel) }
            composable(Screen.WorldClock.route) { WorldClockScreen() }
            composable(Screen.Analytics.route) { AnalyticsScreen(viewModel) }
            composable(Screen.Pedometer.route) { PedometerScreen() }
        }
    }
}

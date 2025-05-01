package com.example.budgetbuddy_prog7313

import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.budgetbuddy_prog7313.ui.theme.BudgetBuddy_Prog7313Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetBuddy_Prog7313Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Login screen (no nav bar)
                    composable("login") {
                        LoginScreen {
                            navController.navigate("main") {
                                // Clear the back stack completely
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    // Main app with bottom bar
                    composable("main") {
                        val mainNavController = rememberNavController()
                        Scaffold(
                            bottomBar = { BottomNavBar(mainNavController) }
                        ) { innerPadding ->
                            NavHost(
                                navController = mainNavController,
                                startDestination = "home",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("home") { HomeScreen() }
                                composable("expenses") { ExpScreen() }
                                composable("budget") { BudgetScreen() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onLoginSuccess
        ) {
            Text("LOGIN")
        }
    }
}

// Update BottomNavBar to use the correct navController
@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        val currentRoute = currentRoute(navController)
        listOf(
            Screen.Home,
            Screen.Expenses,
            Screen.Budget
        ).forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Only pop up to start of main navigation
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}







@Composable
fun BudgetScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Budget Screen")
    }
}




@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

// SThe following are the objects ive created for my screens
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Expenses : Screen("expenses", "Expenses", Icons.Default.Receipt)
    object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BudgetBuddy_Prog7313Theme {
        LoginScreen(onLoginSuccess = {})
    }
}
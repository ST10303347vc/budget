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
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.budgetbuddy_prog7313.ui.theme.BudgetBuddy_Prog7313Theme
import com.example.budgetbuddy_prog7313.data.AppDatabase
import com.example.budgetbuddy_prog7313.data.User
import com.example.budgetbuddy_prog7313.data.UserDao

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
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().insertUser(User("1", "1"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                // Use coroutine to check login
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.validateUser(username, password)
                    if (user != null) {
                        withContext(Dispatchers.Main) {
                            onLoginSuccess()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            error = "Invalid credentials"
                        }
                    }
                }
            }
        ) {
            Text("LOGIN")
        }
        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}



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
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

// The following are the objects ive created for my screens
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
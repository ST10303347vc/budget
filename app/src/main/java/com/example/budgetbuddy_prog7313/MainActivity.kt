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
import androidx.compose.material3.ButtonDefaults
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
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BudgetBuddy_Prog7313Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // Login screen
                    composable("login") {
                        LoginScreen { username ->
                            navController.navigate("main/$username") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    // Main shell with username passed
                    composable("main/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        val mainNavController = rememberNavController()

                        Scaffold(bottomBar = { BottomNavBar(mainNavController) }) { innerPadding ->
                            NavHost(
                                navController = mainNavController,
                                startDestination = "home",
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("home") { HomeScreen(username) }
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
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // This is for my user i can use while i test
    LaunchedEffect(Unit) {
        val existing = userDao.login("1", "1")
        if (existing == null) {
            userDao.insert(User("1", "1"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )

    {
        Image(
            painter = painterResource(id = R.drawable.square),
            contentDescription = "App logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            shape = RoundedCornerShape(12.dp), //Makes my button shape rounder
            label = { Text("Username") },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.Black,


            ), modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp)

        )

        Spacer(modifier = Modifier.height(10.dp),


        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text("Password") },

            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.Black)

        )

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                scope.launch {
                    val user = userDao.login(username, password)

                    if (user != null) { //
                        withContext(Dispatchers.Main) {
                            onLoginSuccess(username) //Login Success of the form meaning its correct takes username atrubute stores and uses for greating
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            error = "Invalid credentials"
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(280.dp)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp)
        )



        {
            Text("LOGIN")
        }

        TextButton(onClick = { showCreateDialog = true }) {
            Text("Create New Account")
        }

        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onUserCreated = { showCreateDialog = false }
        )
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
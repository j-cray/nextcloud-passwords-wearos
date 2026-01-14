
package com.example.nextcloud_passwords_wearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.nextcloud_passwords_wearos.ui.add.AddPasswordScreen
import com.example.nextcloud_passwords_wearos.ui.detail.PasswordDetailScreen
import com.example.nextcloud_passwords_wearos.ui.list.PasswordListScreen
import com.example.nextcloud_passwords_wearos.ui.login.LoginScreen
import com.example.nextcloud_passwords_wearos.ui.login.LoginViewModel
import com.example.nextcloud_passwords_wearos.ui.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberSwipeDismissableNavController()
                
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        val viewModel: LoginViewModel = koinViewModel()
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("list") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    composable("list") {
                        PasswordListScreen(
                            onPasswordClick = { password ->
                                navController.navigate("detail/${password.id}")
                            },
                            onAddClick = {
                                navController.navigate("add")
                            },
                            onSettingsClick = {
                                navController.navigate("settings")
                            }
                        )
                    }
                    
                    composable("detail/{passwordId}") { backStackEntry ->
                        val passwordId = backStackEntry.arguments?.getString("passwordId")
                        if (passwordId != null) {
                            PasswordDetailScreen(passwordId = passwordId)
                        }
                    }
                    
                    composable("add") {
                        AddPasswordScreen(
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }
                    
                    composable("settings") {
                        SettingsScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("list") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

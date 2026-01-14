
package com.example.nextcloud_passwords_wearos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.nextcloud_passwords_wearos.ui.detail.PasswordDetailScreen
import com.example.nextcloud_passwords_wearos.ui.list.PasswordListScreen
import com.example.nextcloud_passwords_wearos.ui.login.LoginScreen
import com.example.nextcloud_passwords_wearos.ui.login.LoginViewModel
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
                        // We need to observe login state here to navigate
                        // But LoginScreen handles UI.
                        // Ideally LoginScreen should have a callback onLoginSuccess
                        
                        // For now, let's modify LoginScreen to take a callback or observe state here
                        // But LoginScreen is complex.
                        
                        // Let's pass the navController to LoginScreen? No, bad practice.
                        // Let's observe state here.
                        
                        // Actually, LoginScreen is already observing state.
                        // I'll modify LoginScreen to take onLoginSuccess
                        
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
                            }
                        )
                    }
                    
                    composable("detail/{passwordId}") { backStackEntry ->
                        val passwordId = backStackEntry.arguments?.getString("passwordId")
                        if (passwordId != null) {
                            PasswordDetailScreen(passwordId = passwordId)
                        }
                    }
                }
            }
        }
    }
}

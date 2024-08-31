package com.shreyanshsinghks.chatapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shreyanshsinghks.chatapp.feature.auth.singin.SignInScreen
import com.shreyanshsinghks.chatapp.feature.auth.singup.SignUpScreen
import com.shreyanshsinghks.chatapp.navigation.NavigationItems

@Composable
fun MainAppComposable() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = NavigationItems.SignIn) {
            composable<NavigationItems.Home> {
                MainAppComposable()
            }

            composable<NavigationItems.SignIn> {
                SignInScreen(navController = navController)
            }

            composable<NavigationItems.SignUp> {
                SignUpScreen(navController = navController)
            }
        }
    }
}
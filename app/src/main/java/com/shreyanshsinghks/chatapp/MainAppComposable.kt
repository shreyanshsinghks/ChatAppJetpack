package com.shreyanshsinghks.chatapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.shreyanshsinghks.chatapp.presentation.auth.singin.SignInScreen
import com.shreyanshsinghks.chatapp.presentation.auth.singup.SignUpScreen
import com.shreyanshsinghks.chatapp.presentation.home.HomeScreen
import com.shreyanshsinghks.chatapp.navigation.NavigationItems

@Composable
fun MainAppComposable() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val startDestination = if(currentUser != null) NavigationItems.Home else NavigationItems.SignIn

        NavHost(navController = navController, startDestination = startDestination) {
            composable<NavigationItems.Home> {
                HomeScreen(navController = navController)
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
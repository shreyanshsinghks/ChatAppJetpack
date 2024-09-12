package com.shreyanshsinghks.chatapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.shreyanshsinghks.chatapp.navigation.Chat
import com.shreyanshsinghks.chatapp.navigation.Home
import com.shreyanshsinghks.chatapp.presentation.auth.singin.SignInScreen
import com.shreyanshsinghks.chatapp.presentation.auth.singup.SignUpScreen
import com.shreyanshsinghks.chatapp.presentation.home.HomeScreen
import com.shreyanshsinghks.chatapp.navigation.SignIn
import com.shreyanshsinghks.chatapp.navigation.SignUp
import com.shreyanshsinghks.chatapp.presentation.chat.ChatScreen

@Composable
fun MainAppComposable() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val startDestination =
            if (currentUser != null) Home else SignIn

        NavHost(navController = navController, startDestination = startDestination) {
            composable<Home> {
                HomeScreen(navController = navController)
            }

            composable<SignIn> {
                SignInScreen(navController = navController)
            }

            composable<Chat> { backStackEntry ->
                val arguments = backStackEntry.toRoute<Chat>()
                ChatScreen(
                    navController = navController,
                    channelId = arguments.channelId ?: "No Channel Id",
                    channelName = arguments.channelName ?: "No Channel Name"
                )
            }

            composable<SignUp> {
                SignUpScreen(navController = navController)
            }
        }
    }
}
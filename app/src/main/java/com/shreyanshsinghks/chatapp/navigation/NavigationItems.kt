package com.shreyanshsinghks.chatapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItems(val route: String) {
    @Serializable
    data object Home : NavigationItems("home")

    @Serializable
    data object SignIn : NavigationItems("signIn")

    @Serializable
    data object SignUp : NavigationItems("signUp")
}
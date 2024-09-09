package com.shreyanshsinghks.chatapp.navigation

import kotlinx.serialization.Serializable


    @Serializable
    object Home

    @Serializable
    object SignIn

    @Serializable
    object SignUp

    @Serializable
    data class Chat(val channelId: String?)

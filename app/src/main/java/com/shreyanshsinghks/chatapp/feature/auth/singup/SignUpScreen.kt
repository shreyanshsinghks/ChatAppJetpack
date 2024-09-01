package com.shreyanshsinghks.chatapp.feature.auth.singup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.shreyanshsinghks.chatapp.R
import com.shreyanshsinghks.chatapp.navigation.NavigationItems

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(key1 = uiState.value) {
        when (uiState.value) {
            is SignUpState.Success -> {
                navController.navigate(NavigationItems.Home){
                    popUpTo(NavigationItems.SignUp){
                        inclusive = true
                    }
                }
            }

            is SignUpState.Error -> {
                name = ""
                email = ""
                password = ""
                confirmPassword = ""
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }



    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.chatapp),
                contentDescription = "Chat app logo",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                placeholder = { Text(text = "Name") },
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text(text = "Email") },
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text(text = "Password") },
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                placeholder = { Text(text = "Confirm Password") },
                label = { Text(text = "Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                isError = password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.value == SignUpState.Loading) {
                CircularProgressIndicator()
            } else {

                Button(
                    onClick = { viewModel.signUp(name, email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword && uiState.value == SignUpState.Nothing || uiState.value == SignUpState.Error
                ) {
                    Text(text = "Sign Up")
                }

                TextButton(onClick = { navController.navigateUp() }) {
                    Text(text = "Already have an account? Sign In")
                }
            }

        }
    }
}
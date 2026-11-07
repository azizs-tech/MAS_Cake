package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onSignUpClicked: () -> Unit,
    onBackClicked: () -> Unit // Added back button parameter
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isEnglish by remember { mutableStateOf(true) }

    val titleText = if (isEnglish) "Create Account" else "Создать аккаунт"
    val subtitleText = if (isEnglish) "Join us to order delicious cakes!" else "Присоединяйтесь, чтобы заказать вкусные торты!"
    val nameLabel = if (isEnglish) "Full Name" else "Полное имя"
    val emailLabel = if (isEnglish) "Email Address" else "Эл. почта"
    val passwordLabel = if (isEnglish) "Password" else "Пароль"
    val signUpText = if (isEnglish) "Sign Up" else "Зарегистрироваться"
    val loginText = if (isEnglish) "Already have an account? Log in" else "Уже есть аккаунт? Войти"

    // Scaffold provides the top app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ChocolateBrown
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamWhite // Matches your background
                )
            )
        },
        containerColor = CreamWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding so top bar doesn't overlap
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { isEnglish = !isEnglish }) {
                    Text(
                        text = if (isEnglish) "RU 🇷🇺" else "EN 🇬🇧",
                        color = ChocolateBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MAS CAKE",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ChocolateBrown,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = titleText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown
            )

            Text(
                text = subtitleText,
                fontSize = 16.sp,
                color = ChocolateBrown.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(nameLabel) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChocolateBrown,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = ChocolateBrown,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = ChocolateBrown,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(emailLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChocolateBrown,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = ChocolateBrown,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = ChocolateBrown,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(passwordLabel) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChocolateBrown,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = ChocolateBrown,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = ChocolateBrown,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSignUpClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChocolateBrown,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = signUpText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* Navigate to Login Screen later */ }) {
                Text(
                    text = loginText,
                    color = ChocolateBrown,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
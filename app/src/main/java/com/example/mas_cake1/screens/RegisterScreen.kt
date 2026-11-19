package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.LanguageToggle
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.components.masTextFieldColors
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun RegisterScreen(
    onSignUpClicked: (name: String, phone: String, email: String) -> Unit,
    onBackClicked: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    val phoneDigits = phone.count { it.isDigit() }
    val nameError = name.isBlank()
    val phoneError = phoneDigits < 10
    val emailError = email.isNotBlank() && !email.contains("@")
    val passwordError = password.length < 4

    Scaffold(
        topBar = { BackTopBar("", onBackClicked) { LanguageToggle(onLanguageToggle) } },
        containerColor = Mas.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MAS CAKE",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Mas.colors.text,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            Text(tr("Create Account", "Создать аккаунт"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Text(
                tr("Join us to order delicious cakes!", "Присоединяйтесь, чтобы заказать вкусные торты!"),
                fontSize = 16.sp,
                color = Mas.colors.textMuted,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            Field(name, { name = it }, tr("Full Name", "Полное имя"), showErrors && nameError, tr("Enter your name", "Введите имя"))
            Field(phone, { phone = it }, tr("Phone number", "Номер телефона"), showErrors && phoneError, tr("At least 10 digits", "Минимум 10 цифр"), KeyboardType.Phone)
            Field(email, { email = it }, tr("Email (optional)", "Эл. почта (необязательно)"), showErrors && emailError, tr("Check the email", "Проверьте почту"), KeyboardType.Email)
            Field(password, { password = it }, tr("Password", "Пароль"), showErrors && passwordError, tr("At least 4 characters", "Минимум 4 символа"), KeyboardType.Password, isPassword = true)

            Spacer(Modifier.height(16.dp))

            PrimaryButton(tr("Sign Up", "Зарегистрироваться"), onClick = {
                showErrors = true
                if (!nameError && !phoneError && !emailError && !passwordError) onSignUpClicked(name, phone, email)
            })
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String,
    keyboard: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        isError = isError,
        supportingText = if (isError) { { Text(errorText) } } else null,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = masTextFieldColors()
    )
}

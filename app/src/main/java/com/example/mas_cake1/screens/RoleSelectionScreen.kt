package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite
import com.example.mas_cake1.ui.theme.LightCaramel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onCustomerSelected: () -> Unit,
    onAdminSelected: () -> Unit,
    onBackClicked: () -> Unit // Added back button parameter
) {
    var isEnglish by remember { mutableStateOf(true) }

    val welcomeText = if (isEnglish) "Welcome to MAS Cake!" else "Добро пожаловать в MAS Cake!"
    val questionText = if (isEnglish) "How do you want to use the app?" else "Как вы хотите использовать приложение?"
    val customerText = if (isEnglish) "I want to order cakes\n(Customer)" else "Я хочу заказать торты\n(Клиент)"
    val adminText = if (isEnglish) "Admin" else "Админ"

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
                    containerColor = CreamWhite
                )
            )
        },
        containerColor = CreamWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = welcomeText,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ChocolateBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = questionText,
                fontSize = 18.sp,
                color = ChocolateBrown.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Button(
                onClick = onCustomerSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
            ) {
                Text(
                    text = customerText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAdminSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightCaramel)
            ) {
                Text(
                    text = adminText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

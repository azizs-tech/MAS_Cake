package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.LightCaramel
import com.example.mas_cake1.ui.theme.CreamWhite

@Composable
fun SplashScreen(onStartClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Title
        Text(
            text = "MAS CAKE",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ChocolateBrown,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Main Title
        Text(
            text = "Сладкие моменты начинаются здесь",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "Торты и десерты ручной работы с доставкой по городу.",
            fontSize = 16.sp,
            color = LightCaramel,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Start Button
        Button(
            onClick = onStartClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChocolateBrown,
                contentColor = CreamWhite
            )
        ) {
            Text(
                text = "Начать",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
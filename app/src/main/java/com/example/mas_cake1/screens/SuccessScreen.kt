package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.LightCaramel

@Composable
fun SuccessScreen(onBackToHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = LightCaramel,
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Спасибо за заказ!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Мы уже начали готовить ваш торт. Статус заказа можно отслеживать в личном кабинете.",
            textAlign = TextAlign.Center,
            color = ChocolateBrown.copy(alpha = 0.7f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
        ) {
            Text("На главную", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
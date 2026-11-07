package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBackClicked: () -> Unit, onOrderConfirmed: () -> Unit) {
    var isDelivery by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление", color = ChocolateBrown) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ChocolateBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()
        ) {
            Text(
                text = "Как вам удобнее получить заказ?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isDelivery,
                    onClick = { isDelivery = true },
                    colors = RadioButtonDefaults.colors(selectedColor = ChocolateBrown)
                )
                Text("Доставка курьером (от 300 ₽)", color = ChocolateBrown)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isDelivery,
                    onClick = { isDelivery = false },
                    colors = RadioButtonDefaults.colors(selectedColor = ChocolateBrown)
                )
                Text("Самовывоз (Бесплатно)", color = ChocolateBrown)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Адрес доставки") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Дата и время") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Оплата:", fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text("Оплата картой онлайн / При получении", color = ChocolateBrown.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onOrderConfirmed,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
            ) {
                Text("Подтвердить заказ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
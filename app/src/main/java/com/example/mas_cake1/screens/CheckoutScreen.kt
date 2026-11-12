package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun CheckoutScreen(
    totalPrice: Int,
    onBackClicked: () -> Unit,
    onOrderConfirmed: () -> Unit,
    isEnglish: Boolean
) {
    var isDelivery by remember { mutableStateOf(true) }
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val titleText = if (isEnglish) "Checkout" else "Оформление"
    val questionText = if (isEnglish) "How would you like to receive your order?" else "Как вам удобнее получить заказ?"
    val deliveryText = if (isEnglish) "Courier Delivery (from 300 ₽)" else "Доставка курьером (от 300 ₽)"
    val pickupText = if (isEnglish) "Pickup from bakery (Free)" else "Самовывоз из кондитерской (Бесплатно)"
    val addressLabel = if (isEnglish) "Delivery Address" else "Укажите адрес доставки"
    val dateLabel = if (isEnglish) "Date and Time" else "Выберите дату и время"
    val confirmText = if (isEnglish) "Confirm Order — $totalPrice ₽" else "Подтвердить заказ — $totalPrice ₽"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText, color = ChocolateBrown) },
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
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Text(questionText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isDelivery,
                    onClick = { isDelivery = true },
                    colors = RadioButtonDefaults.colors(selectedColor = ChocolateBrown)
                )
                Text(deliveryText, color = ChocolateBrown)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isDelivery,
                    onClick = { isDelivery = false },
                    colors = RadioButtonDefaults.colors(selectedColor = ChocolateBrown)
                )
                Text(pickupText, color = ChocolateBrown)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isDelivery) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(addressLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = ChocolateBrown
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text(dateLabel) },
                placeholder = { Text(if (isEnglish) "e.g., 15 May, 14:00" else "Например: 15 мая, 14:00") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ChocolateBrown
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(if (isEnglish) "Payment:" else "Оплата:", fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text(if (isEnglish) "Online Card / Upon receipt" else "Оплата картой онлайн / При получении", color = ChocolateBrown.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onOrderConfirmed,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown),
                enabled = date.isNotBlank() && (!isDelivery || address.isNotBlank())
            ) {
                Text(confirmText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
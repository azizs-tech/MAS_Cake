package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown

@Composable
fun CartScreen(onCheckoutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ваш заказ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Торт «Шоколадный трюфель»,\n1 кг",
                    color = ChocolateBrown,
                    fontSize = 16.sp
                )
                Text(
                    text = "3 200 ₽",
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Итого:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown
            )
            Text(
                text = "3 200 ₽",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCheckoutClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
        ) {
            Text(
                text = "Перейти к оформлению",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite
import com.example.mas_cake1.ui.theme.LightCaramel

@Composable
fun HomeScreen(onNavigateToProduct: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Greeting
        Text(
            text = "Добрый день!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )
        Text(
            text = "Что будем праздновать?",
            fontSize = 16.sp,
            color = ChocolateBrown.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Поиск торта или десерта...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ChocolateBrown) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChocolateBrown,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(LightCaramel, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Скидка 15% на торты\nко Дню рождения!",
                color = CreamWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Categories
        Text(
            text = "Категории",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val categories = listOf("Торты", "Бенто", "Капкейки", "Печенье", "Наборы")
            items(categories.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ChocolateBrown.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = categories[index],
                        modifier = Modifier.padding(16.dp),
                        color = ChocolateBrown,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Popular Section
        Text(
            text = "Популярное у нас",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Product Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToProduct("Торт «Красный бархат»") },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                ) // Placeholder for the cake image
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Торт «Красный бархат»",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown
                )
                Text(
                    text = "от 2 500 ₽",
                    color = LightCaramel,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
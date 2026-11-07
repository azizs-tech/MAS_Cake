package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
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
fun ProductDetailScreen(cakeName: String, onBackClicked: () -> Unit, onAddToCart: () -> Unit) {
    var weight by remember { mutableStateOf("1 кг") }
    var textOnCake by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали", color = ChocolateBrown) },
                navigationIcon = { IconButton(onClick = onBackClicked) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ChocolateBrown) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray, RoundedCornerShape(16.dp))) // Image placeholder

            Spacer(modifier = Modifier.height(16.dp))
            Text(cakeName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text("3 200 ₽ (за 1 кг)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Нежный бисквит с насыщенным шоколадным кремом и вишневой начинкой.", color = ChocolateBrown.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(8.dp))
            Text("Состав: мука, какао, яйца, сливочное масло, вишня, темный шоколад.", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            Text("Вес:", fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("1 кг", "1.5 кг", "2 кг").forEach { option ->
                    FilterChip(selected = weight == option, onClick = { weight = option }, label = { Text(option) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = textOnCake, onValueChange = { textOnCake = it },
                label = { Text("Надпись на торте") }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
            ) {
                Text("Добавить в корзину — 3 200 ₽", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
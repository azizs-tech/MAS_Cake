package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite
import com.example.mas_cake1.ui.theme.LightCaramel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    searchQuery: String,
    onBackClicked: () -> Unit,
    onCakeSelected: (String) -> Unit
) {
    // Combine EVERY cake from every category into one list
    val allCakes = listOf(
        // Wedding
        CakeListData("Traditional Royal Fruitcake", 3500, R.drawable.cake_1wed),
        CakeListData("Lemon & Elderflower Sponge", 2800, R.drawable.cake_2wed),
        CakeListData("Yellow Sponge with Bavarian Cream", 3000, R.drawable.cake_3wed),
        CakeListData("Chocolate Biscuit Groom's Cake", 1800, R.drawable.cake_4wed),
        CakeListData("Classic White Vanilla Buttercream", 1600, R.drawable.cake_5wed),
        // Birthday
        CakeListData("Honey Cake (Medovik)", 3300, R.drawable.cake_1birth),
        CakeListData("Napoleon", 2300, R.drawable.cake_2birth),
        CakeListData("Chocolate Cake (Prague)", 2400, R.drawable.cake_3birth),
        CakeListData("Cheesecake", 2100, R.drawable.cake_4birth),
        CakeListData("Bento Cake", 2278, R.drawable.cake_5birth),
        // Usual
        CakeListData("Птичье молоко (Ptichye Moloko)", 2600, R.drawable.cake_usual1),
        CakeListData("Сметанник (Smetannik)", 535, R.drawable.cake_usual2),
        CakeListData("Невский пирог (Nevsky Pirog)", 226, R.drawable.cake_usual3),
        CakeListData("Мишка на севере (Mishka na Severe)", 2950, R.drawable.cake_usual4),
        CakeListData("Прага (Prague)", 4200, R.drawable.cake_usual5),
        CakeListData("Тирамису (Tiramisu)", 3345, R.drawable.cake_usual6),
        CakeListData("Захерторт (Sachertorte)", 1120, R.drawable.cake_usual7),
        CakeListData("Красный бархат (Red Velvet)", 924, R.drawable.cake_usual8),
        CakeListData("Опера (Opéra)", 3500, R.drawable.cake_usual9),
        CakeListData("Чёрный лес (Black Forest)", 2567, R.drawable.cake_usual10)
    )

    // Filter the giant list based on what they typed
    val results = allCakes.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search: \"$searchQuery\"", color = ChocolateBrown, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ChocolateBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { padding ->
        if (results.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No cakes found", color = ChocolateBrown.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(results.size) { index ->
                    val cake = results[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCakeSelected(cake.name) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = cake.imageRes,
                                contentDescription = cake.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(80.dp).background(Color.LightGray, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(cake.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
                                Text("от ${cake.price} ₽", color = LightCaramel, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
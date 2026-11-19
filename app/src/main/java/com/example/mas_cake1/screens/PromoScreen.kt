package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite
import com.example.mas_cake1.ui.theme.LightCaramel

data class PromoCakeData(
    val name: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoScreen(
    onBackClicked: () -> Unit,
    onAddToCart: (String, String, Int) -> Unit, // Name, Weight, Price
    isEnglish: Boolean
) {
    // 5 cakes for the discount
    val promoList = listOf(
        PromoCakeData("Honey Cake (Medovik)", 3300, 2805, R.drawable.cake_1birth), // 15% off
        PromoCakeData("Napoleon", 2300, 1955, R.drawable.cake_2birth), // 15% off
        PromoCakeData("Cheesecake", 2100, 1785, R.drawable.cake_4birth), // 15% off
        PromoCakeData("Traditional Royal Fruitcake", 3500, 2975, R.drawable.cake_1wed), // 15% off
        PromoCakeData("Chocolate Cake (Prague)", 2400, 2040, R.drawable.cake_3birth) // 15% off
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEnglish) "15% Discount!" else "Скидка 15%!", color = ChocolateBrown, fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(promoList.size) { index ->
                val cake = promoList[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Small image
                            AsyncImage(
                                model = cake.imageRes,
                                contentDescription = cake.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(cake.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)

                                // Original price (crossed out)
                                Text(
                                    text = "${cake.originalPrice} ₽",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )

                                // Discounted price
                                Text(
                                    text = "${cake.discountedPrice} ₽",
                                    color = Color.Red,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add to Cart Button
                        Button(
                            onClick = {
                                // We pass "1 кг" as the default weight for the promo
                                onAddToCart(cake.name, if (isEnglish) "1 kg" else "1 кг", cake.discountedPrice)
                            },
                            modifier = Modifier.fillMaxWidth().height(45.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LightCaramel)
                        ) {
                            Text(
                                text = if (isEnglish) "Add to Cart — ${cake.discountedPrice} ₽" else "В корзину — ${cake.discountedPrice} ₽",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
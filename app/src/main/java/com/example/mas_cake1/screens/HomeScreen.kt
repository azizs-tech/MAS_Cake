package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.LightCaramel

@Composable
fun HomeScreen(
    onCategorySelected: (String) -> Unit,
    onNavigateToPromo: () -> Unit,
    onSearchExecuted: (String) -> Unit, // <-- ADDED
    isEnglish: Boolean,
    onLanguageToggle: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    data class CategoryItem(val titleEn: String, val titleRu: String, val imageRes: Int)

    val categories = listOf(
        CategoryItem("Wedding Cakes", "Свадебные торты", R.drawable.wedding_main_page),
        CategoryItem("Birthday Cakes", "Детские торты", R.drawable.birthday_main_page),
        CategoryItem("Usual Cakes", "Обычные торты", R.drawable.usualcake_main_page)
    )

    val greetingText = if (isEnglish) "Good day!" else "Добрый день!"
    val questionText = if (isEnglish) "What are we celebrating?" else "Что будем праздновать?"
    val searchPlaceholder = if (isEnglish) "Search for a cake..." else "Поиск торта..."
    val bannerText = if (isEnglish) "15% discount on cakes!" else "Скидка 15% на торты!"
    val categoryTitle = if (isEnglish) "Categories" else "Категории"

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onLanguageToggle) {
                    Text(
                        text = if (isEnglish) "RU 🇷🇺" else "EN 🇬🇧",
                        color = ChocolateBrown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Text(greetingText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text(questionText, fontSize = 16.sp, color = ChocolateBrown.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(searchPlaceholder, color = Color.Gray) },
                leadingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) onSearchExecuted(searchQuery) // <-- TRIGGER SEARCH ON CLICK
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = ChocolateBrown)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ChocolateBrown,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(LightCaramel, RoundedCornerShape(16.dp))
                    .clickable { onNavigateToPromo() },
                contentAlignment = Alignment.Center
            ) {
                Text(bannerText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(categoryTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(categories.size) { index ->
            val category = categories[index]
            val displayTitle = if (isEnglish) category.titleEn else category.titleRu

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCategorySelected(displayTitle) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    AsyncImage(
                        model = category.imageRes,
                        contentDescription = displayTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
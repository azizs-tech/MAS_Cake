package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Cake
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.CakeRowCard
import com.example.mas_cake1.ui.components.ShimmerCakeList
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.delay

enum class SortOrder { DEFAULT, PRICE_LOW_HIGH, PRICE_HIGH_LOW }

fun sortCakes(list: List<Cake>, order: SortOrder): List<Cake> = when (order) {
    SortOrder.DEFAULT -> list
    SortOrder.PRICE_LOW_HIGH -> list.sortedBy { it.pricePerKg }
    SortOrder.PRICE_HIGH_LOW -> list.sortedByDescending { it.pricePerKg }
}

/** Sort button with a small dropdown (feature 15). */
@Composable
fun SortButton(current: SortOrder, onChange: (SortOrder) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(Icons.Default.SwapVert, contentDescription = tr("Sort", "Сортировка"), tint = Mas.colors.text)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            listOf(
                SortOrder.DEFAULT to tr("Recommended", "Рекомендуемые"),
                SortOrder.PRICE_LOW_HIGH to tr("Price: Low to High", "Цена: по возрастанию"),
                SortOrder.PRICE_HIGH_LOW to tr("Price: High to Low", "Цена: по убыванию")
            ).forEach { (order, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onChange(order); open = false },
                    trailingIcon = { if (order == current) Icon(Icons.Default.Check, null) }
                )
            }
        }
    }
}

@Composable
fun CategoryCakesScreen(
    category: CakeCategory,
    onBackClicked: () -> Unit,
    onCakeSelected: (String) -> Unit
) {
    val en = LocalEnglish.current
    var sort by remember { mutableStateOf(SortOrder.DEFAULT) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(category) { delay(1000); loading = false }   // shimmer for 1 second (feature 2)

    val cakes = sortCakes(AppStore.cakes.filter { it.category == category }, sort)

    Scaffold(
        topBar = { BackTopBar(category.longTitle(en), onBackClicked) { SortButton(sort) { sort = it } } },
        containerColor = Mas.colors.background
    ) { padding ->
        if (loading) {
            ShimmerCakeList(modifier = Modifier.padding(padding).padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cakes, key = { it.id }) { cake ->
                    CakeRowCard(cake, onClick = { onCakeSelected(cake.id) })
                }
            }
        }
    }
}

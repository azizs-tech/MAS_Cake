package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.ui.components.CakeGridCard
import com.example.mas_cake1.ui.components.masChipColors
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

/** The "Menu" tab: every cake in a grid, with category chips and sorting. (Was an empty placeholder before.) */
@Composable
fun MenuContent(onOpenCake: (String) -> Unit) {
    val en = LocalEnglish.current
    var category by remember { mutableStateOf<CakeCategory?>(null) }
    var sort by remember { mutableStateOf(SortOrder.DEFAULT) }

    val cakes = sortCakes(AppStore.cakes.filter { category == null || it.category == category }, sort)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Menu", "Меню"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.weight(1f))
                SortButton(sort) { sort = it }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = category == null, onClick = { category = null }, label = { Text(tr("All", "Все")) }, colors = masChipColors())
                }
                items(CakeCategory.entries.size) { i ->
                    val c = CakeCategory.entries[i]
                    FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c.title(en)) }, colors = masChipColors())
                }
            }
        }
        items(cakes, key = { it.id }) { cake ->
            CakeGridCard(cake, onClick = { onOpenCake(cake.id) })
        }
    }
}

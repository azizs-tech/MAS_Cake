package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Cake
import com.example.mas_cake1.data.CakeTag
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.CakeRowCard
import com.example.mas_cake1.ui.components.EmptyState
import com.example.mas_cake1.ui.components.ShimmerCakeList
import com.example.mas_cake1.ui.components.masChipColors
import com.example.mas_cake1.ui.components.masTextFieldColors
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.delay

private fun Cake.matches(filter: String): Boolean = when (filter) {
    SearchFilters.UNDER_2000 -> pricePerKg < 2000
    SearchFilters.CHOCOLATE -> CakeTag.CHOCOLATE in tags
    SearchFilters.FRUIT -> CakeTag.FRUIT in tags
    SearchFilters.BESTSELLER -> CakeTag.BESTSELLER in tags
    else -> true
}

@Composable
fun SearchResultsScreen(
    initialQuery: String,
    initialFilter: String,
    onBackClicked: () -> Unit,
    onCakeSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }
    var appliedQuery by remember { mutableStateOf(initialQuery) }
    val filters = remember { mutableStateListOf<String>().apply { if (initialFilter.isNotEmpty()) add(initialFilter) } }
    var loading by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    // Show the shimmer for 1 second every time the search changes.
    LaunchedEffect(appliedQuery, filters.toList()) { loading = true; delay(1000); loading = false }

    // Search both English and Russian names, so "медовик" and "honey" both work.
    val results = AppStore.cakes.filter { cake ->
        (appliedQuery.isBlank() || cake.nameEn.contains(appliedQuery, true) || cake.nameRu.contains(appliedQuery, true)) &&
            filters.all { cake.matches(it) }
    }

    fun submit() {
        appliedQuery = query.trim()
        AppStore.addRecentSearch(appliedQuery)
        focusManager.clearFocus()
    }

    Scaffold(
        topBar = { BackTopBar(tr("Search", "Поиск"), onBackClicked) },
        containerColor = Mas.colors.background
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(tr("Search for a cake...", "Поиск торта..."), color = Mas.colors.textMuted) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Mas.colors.text) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { query = ""; appliedQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = tr("Clear", "Очистить"), tint = Mas.colors.textMuted)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submit() }),
                shape = RoundedCornerShape(14.dp),
                colors = masTextFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                items(SearchFilters.all) { key ->
                    val selected = key in filters
                    FilterChip(
                        selected = selected,
                        onClick = { if (selected) filters.remove(key) else filters.add(key) },
                        label = { Text(filterLabel(key)) },
                        colors = masChipColors()
                    )
                }
            }

            when {
                loading -> ShimmerCakeList(modifier = Modifier.padding(16.dp))
                results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(tr("No cakes found", "Ничего не найдено"), tr("Try another word or remove a filter.", "Попробуйте другое слово или уберите фильтр."))
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(results, key = { it.id }) { cake ->
                        CakeRowCard(cake, onClick = { onCakeSelected(cake.id) })
                    }
                }
            }
        }
    }
}

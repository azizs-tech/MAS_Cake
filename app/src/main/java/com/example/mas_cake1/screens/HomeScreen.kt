package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.CakeCatalog
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.CakeStrip
import com.example.mas_cake1.ui.components.LanguageToggle
import com.example.mas_cake1.ui.components.SectionTitle
import com.example.mas_cake1.ui.components.masChipColors
import com.example.mas_cake1.ui.components.masTextFieldColors
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.LightCaramel
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Pistachio
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.delay

/** Filter keys shared by Home and Search. */
object SearchFilters {
    const val UNDER_2000 = "under2000"
    const val CHOCOLATE = "CHOCOLATE"
    const val FRUIT = "FRUIT"
    const val BESTSELLER = "BESTSELLER"
    val all = listOf(UNDER_2000, CHOCOLATE, FRUIT, BESTSELLER)
}

@Composable
fun filterLabel(key: String): String = when (key) {
    SearchFilters.UNDER_2000 -> tr("Under 2000 ₽", "До 2000 ₽")
    SearchFilters.CHOCOLATE -> tr("Chocolate", "Шоколадные")
    SearchFilters.FRUIT -> tr("Fruit", "Фруктовые")
    else -> tr("Bestsellers", "Хиты")
}

private data class PromoSlide(val title: String, val subtitle: String, val colors: List<Color>, val emoji: String, val onClick: () -> Unit)

@Composable
fun HomeScreen(
    onCategorySelected: (CakeCategory) -> Unit,
    onOpenPromos: () -> Unit,
    onOpenCake: (String) -> Unit,
    onSearch: (query: String, filter: String) -> Unit,
    onLanguageToggle: () -> Unit
) {
    val en = LocalEnglish.current
    val settings = AppStore.settings
    var searchQuery by remember { mutableStateOf("") }
    var searchFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun runSearch(q: String) {
        if (q.isBlank()) return
        AppStore.addRecentSearch(q)
        focusManager.clearFocus()
        onSearch(q.trim(), "")
    }

    val userName = settings.userName.substringBefore(" ")
    val greeting = if (userName.isNotBlank()) tr("Good day, $userName!", "Добрый день, $userName!") else tr("Good day!", "Добрый день!")

    // ---------- carousel slides (feature 1)
    val slides = buildList {
        if (settings.promoEnabled) add(
            PromoSlide(
                tr("${settings.discountPercent}% off cakes!", "Скидка ${settings.discountPercent}% на торты!"),
                tr("On selected cakes this week", "На избранные торты этой недели"),
                listOf(LightCaramel, Color(0xFFB5835A)), "🍰", onOpenPromos
            )
        )
        add(
            PromoSlide(
                tr("Free delivery", "Бесплатная доставка"),
                tr("On orders from ${formatRub(settings.freeDeliveryFrom)}", "При заказе от ${formatRub(settings.freeDeliveryFrom)}"),
                listOf(ChocolateBrown, Color(0xFF7A4B3A)), "🚚", onOpenPromos
            )
        )
        add(
            PromoSlide(
                tr("New: Bento cakes", "Новинка: бенто-торты"),
                tr("A little cake just for you", "Маленький торт только для вас"),
                listOf(Pistachio, Color(0xFF8DB86B)), "🎁", { onOpenCake("bento") }
            )
        )
    }
    val pagerState = rememberPagerState(pageCount = { slides.size })
    LaunchedEffect(pagerState, slides.size) {
        while (true) {
            delay(3500)
            if (slides.isNotEmpty() && !pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % slides.size)
            }
        }
    }

    val recentlyViewed = AppStore.recentlyViewed.mapNotNull { AppStore.cake(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                    Text(tr("What are we celebrating?", "Что будем праздновать?"), fontSize = 16.sp, color = Mas.colors.textMuted)
                }
                LanguageToggle(onLanguageToggle)
            }
        }

        // ---------- search + recent searches (feature 16)
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(tr("Search for a cake...", "Поиск торта..."), color = Mas.colors.textMuted) },
                    leadingIcon = {
                        IconButton(onClick = { runSearch(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = tr("Search", "Поиск"), tint = Mas.colors.text)
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = tr("Clear", "Очистить"), tint = Mas.colors.textMuted)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { runSearch(searchQuery) }),
                    modifier = Modifier.fillMaxWidth().onFocusChanged { searchFocused = it.isFocused },
                    shape = RoundedCornerShape(14.dp),
                    colors = masTextFieldColors()
                )
                if (searchFocused && searchQuery.isEmpty() && AppStore.recentSearches.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(tr("Recent searches", "Недавние запросы"), color = Mas.colors.textMuted, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { AppStore.clearRecentSearches() }) { Text(tr("Clear", "Очистить"), color = Mas.colors.accent, fontSize = 13.sp) }
                        }
                        AppStore.recentSearches.forEach { recent ->
                            Row(
                                Modifier.fillMaxWidth().clickable { searchQuery = recent; runSearch(recent) }.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.History, null, tint = Mas.colors.textMuted, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(recent, color = Mas.colors.text)
                            }
                        }
                    }
                }

                // ---------- filter chips (feature 14)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 10.dp)) {
                    items(SearchFilters.all.size) { i ->
                        val key = SearchFilters.all[i]
                        FilterChip(
                            selected = false,
                            onClick = { onSearch("", key) },
                            label = { Text(filterLabel(key)) },
                            colors = masChipColors()
                        )
                    }
                }
            }
        }

        // ---------- promo carousel
        if (slides.isNotEmpty()) item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(state = pagerState, pageSpacing = 12.dp, modifier = Modifier.fillMaxWidth()) { page ->
                    val slide = slides[page]
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(slide.colors))
                            .clickable(onClick = slide.onClick)
                            .padding(20.dp)
                    ) {
                        Column(Modifier.align(Alignment.CenterStart).fillMaxWidth(0.72f)) {
                            Text(slide.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(6.dp))
                            Text(slide.subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        }
                        Text(slide.emoji, fontSize = 64.sp, modifier = Modifier.align(Alignment.CenterEnd))
                    }
                }
                Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(slides.size) { i ->
                        val selected = pagerState.currentPage == i
                        Box(
                            Modifier
                                .height(8.dp)
                                .width(if (selected) 22.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (selected) Mas.colors.primary else Mas.colors.divider)
                        )
                    }
                }
            }
        }

        item { SectionTitle(tr("Categories", "Категории")) }

        items(CakeCategory.entries.size) { index ->
            val category = CakeCategory.entries[index]
            val title = category.longTitle(en)
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(Modifier.fillMaxWidth().height(170.dp)) {
                    AsyncImage(
                        model = CakeCatalog.categoryImage(category),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
                    Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        val count = AppStore.cakes.count { it.category == category }
                        Text(tr("$count cakes", "$count тортов"), fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
        }

        // ---------- recently viewed (feature 9)
        if (recentlyViewed.isNotEmpty()) {
            item { SectionTitle(tr("Recently viewed", "Вы недавно смотрели")) }
            item { CakeStrip(recentlyViewed, onOpenCake) }
        }
    }
}

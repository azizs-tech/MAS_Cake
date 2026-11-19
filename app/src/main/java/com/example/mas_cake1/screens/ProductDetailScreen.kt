package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.CakeTag
import com.example.mas_cake1.data.WEIGHT_OPTIONS
import com.example.mas_cake1.data.formatKg
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.CakeImage
import com.example.mas_cake1.ui.components.EmptyState
import com.example.mas_cake1.ui.components.FavoriteButton
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.components.StockBadge
import com.example.mas_cake1.ui.components.masChipColors
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Pistachio
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    cakeId: String,
    onBackClicked: () -> Unit,
    onGoToCart: () -> Unit
) {
    val en = LocalEnglish.current
    val cake = AppStore.cake(cakeId)
    var weight by remember { mutableDoubleStateOf(1.0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val addedText = tr("Added to cart ✔", "Добавлено в корзину ✔")
    val cartText = tr("View cart", "В корзину")

    // Remember this cake for the "Recently viewed" row on Home.
    LaunchedEffect(cakeId) { if (cake != null) AppStore.addRecentlyViewed(cakeId) }

    Scaffold(
        topBar = {
            BackTopBar(tr("Details", "Детали"), onBackClicked) {
                if (cake != null) FavoriteButton(cake.id, onImage = false)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Mas.colors.background
    ) { padding ->
        if (cake == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(tr("Cake not found", "Торт не найден"), tr("It may have been removed from the menu.", "Возможно, его убрали из меню."))
            }
            return@Scaffold
        }
        val price = cake.priceFor(weight)
        val discountActive = cake.onPromo && AppStore.settings.promoEnabled

        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            CakeImage(cake, Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(20.dp)))
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!cake.canOrder) StockBadge(tr("Out of stock", "Нет в наличии"), Strawberry)
                else if (cake.isLowStock) StockBadge(tr("Only ${cake.stock} left", "Осталось ${cake.stock}"), Strawberry)
                if (CakeTag.BESTSELLER in cake.tags) StockBadge(tr("Bestseller", "Хит продаж"), Pistachio)
                if (discountActive) StockBadge("-${AppStore.settings.discountPercent}%", Strawberry)
            }
            Spacer(Modifier.height(8.dp))

            Text(cake.name(en), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Text(
                formatRub(price) + if (cake.isPerPiece) tr(" / piece", " / шт") else "",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Mas.colors.accent,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            if (discountActive) {
                Text(
                    tr("${AppStore.settings.discountPercent}% discount will be applied in the cart", "Скидка ${AppStore.settings.discountPercent}% применится в корзине"),
                    color = Strawberry, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Text(cake.description(en).ifBlank { tr("Description coming soon.", "Описание скоро появится.") }, fontSize = 16.sp, color = Mas.colors.textMuted, lineHeight = 24.sp)

            if (!cake.isPerPiece) {
                Spacer(Modifier.height(24.dp))
                Text(tr("Select weight:", "Выберите вес:"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WEIGHT_OPTIONS.forEach { option ->
                        FilterChip(
                            selected = weight == option,
                            onClick = { weight = option },
                            label = { Text(formatKg(option, en)) },
                            colors = masChipColors()
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            PrimaryButton(
                text = if (cake.canOrder) tr("Add to Cart — ${formatRub(price)}", "В корзину — ${formatRub(price)}") else tr("Out of stock", "Нет в наличии"),
                enabled = cake.canOrder,
                onClick = {
                    AppStore.addToCart(cake.id, if (cake.isPerPiece) 1.0 else weight)
                    // Stay on the page and confirm with a snackbar (feature 5)
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val r = snackbarHostState.showSnackbar(addedText, actionLabel = cartText, duration = SnackbarDuration.Short)
                        if (r == SnackbarResult.ActionPerformed) onGoToCart()
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

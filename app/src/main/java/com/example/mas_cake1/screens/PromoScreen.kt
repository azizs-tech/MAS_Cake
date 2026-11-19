package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.CakeImage
import com.example.mas_cake1.ui.components.EmptyState
import com.example.mas_cake1.ui.components.FavoriteButton
import com.example.mas_cake1.ui.components.StockBadge
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import kotlin.math.roundToInt

/** Content of the "Promos" tab. The discount is applied automatically in the cart. */
@Composable
fun PromoContent(onOpenCake: (String) -> Unit, onAdded: () -> Unit) {
    val en = LocalEnglish.current
    val settings = AppStore.settings
    val promoCakes = AppStore.cakes.filter { it.onPromo }

    if (!settings.promoEnabled || promoCakes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                tr("No promotions right now", "Сейчас нет акций"),
                tr("Check back soon — new offers are on the way!", "Загляните позже — новые предложения уже готовятся!")
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(tr("${settings.discountPercent}% Discount!", "Скидка ${settings.discountPercent}%!"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Text(
                tr("The discount is applied automatically in your cart.", "Скидка применяется автоматически в корзине."),
                color = Mas.colors.textMuted, fontSize = 14.sp
            )
        }
        items(promoCakes.size) { index ->
            val cake = promoCakes[index]
            val discounted = (cake.pricePerKg * (100 - settings.discountPercent) / 100.0).roundToInt()
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenCake(cake.id) },
                colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            CakeImage(cake, Modifier.size(96.dp).clip(RoundedCornerShape(12.dp)))
                            StockBadge("-${settings.discountPercent}%", Strawberry, Modifier.align(Alignment.TopStart).padding(4.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cake.name(en), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                            Text(formatRub(cake.pricePerKg), color = Color.Gray, fontSize = 14.sp, textDecoration = TextDecoration.LineThrough)
                            Text(
                                formatRub(discounted) + if (cake.isPerPiece) tr(" / pc", " / шт") else tr(" / kg", " / кг"),
                                color = Strawberry, fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                        }
                        FavoriteButton(cake.id, onImage = false)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { AppStore.addToCart(cake.id, 1.0); onAdded() },
                        enabled = cake.canOrder,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Mas.colors.accent, contentColor = Color.White)
                    ) {
                        Text(
                            if (cake.canOrder) tr("Add to Cart — ${formatRub(discounted)}", "В корзину — ${formatRub(discounted)}")
                            else tr("Out of stock", "Нет в наличии"),
                            fontSize = 14.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

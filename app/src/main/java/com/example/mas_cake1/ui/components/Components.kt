package com.example.mas_cake1.ui.components

import androidx.annotation.RawRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Cake
import com.example.mas_cake1.data.CakeCatalog
import com.example.mas_cake1.data.CakeTag
import com.example.mas_cake1.data.OrderStatus
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Pistachio
import com.example.mas_cake1.ui.theme.Blueberry
import com.example.mas_cake1.ui.theme.Butter
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import java.io.File

// ------------------------------------------------------------------ images

/** Shows a built-in drawable, or the photo the admin uploaded. */
@Composable
fun CakeImage(cake: Cake?, modifier: Modifier = Modifier) {
    val model: Any? = when {
        cake == null -> null
        cake.imagePath.isNotEmpty() -> File(cake.imagePath)
        else -> CakeCatalog.drawableFor(cake.imageKey)
    }
    Box(modifier.background(Mas.colors.shimmer)) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = cake?.nameEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("🎂", fontSize = 32.sp, modifier = Modifier.align(Alignment.Center))
        }
    }
}

// ------------------------------------------------------------------ favorites

@Composable
fun FavoriteButton(cakeId: String, modifier: Modifier = Modifier, onImage: Boolean = true) {
    val fav = AppStore.isFavorite(cakeId)
    IconButton(
        onClick = { AppStore.toggleFavorite(cakeId) },
        modifier = modifier
            .size(36.dp)
            .then(if (onImage) Modifier.background(Color.White.copy(alpha = 0.9f), CircleShape) else Modifier)
    ) {
        Icon(
            if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = tr("Favorite", "Избранное"),
            tint = if (fav) Strawberry else if (onImage) Color(0xFF4A2C2A) else Mas.colors.text,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ------------------------------------------------------------------ cake cards

@Composable
fun CakeGridCard(cake: Cake, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val en = LocalEnglish.current
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box {
            CakeImage(cake, Modifier.fillMaxWidth().aspectRatio(1f).alpha(if (cake.canOrder) 1f else 0.45f))
            FavoriteButton(cake.id, Modifier.align(Alignment.TopEnd).padding(8.dp))
            if (!cake.canOrder) StockBadge(tr("Out of stock", "Нет в наличии"), Strawberry, Modifier.align(Alignment.BottomStart).padding(8.dp))
            else if (cake.onPromo && AppStore.settings.promoEnabled) StockBadge("-${AppStore.settings.discountPercent}%", Strawberry, Modifier.align(Alignment.TopStart).padding(8.dp))
        }
        Column(Modifier.padding(12.dp)) {
            Text(cake.name(en), color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, minLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(priceLabel(cake), color = Mas.colors.accent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun CakeRowCard(cake: Cake, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val en = LocalEnglish.current
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            CakeImage(cake, Modifier.size(84.dp).clip(RoundedCornerShape(10.dp)).alpha(if (cake.canOrder) 1f else 0.45f))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(cake.name(en), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(priceLabel(cake), color = Mas.colors.accent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (!cake.canOrder) {
                    Spacer(Modifier.height(6.dp))
                    StockBadge(tr("Out of stock", "Нет в наличии"), Strawberry)
                } else if (CakeTag.BESTSELLER in cake.tags) {
                    Spacer(Modifier.height(6.dp))
                    StockBadge(tr("Bestseller", "Хит"), Pistachio)
                }
            }
            FavoriteButton(cake.id, onImage = false)
        }
    }
}

@Composable
fun priceLabel(cake: Cake): String =
    if (cake.isPerPiece) tr("${formatRub(cake.pricePerKg)} / pc", "${formatRub(cake.pricePerKg)} / шт")
    else tr("${formatRub(cake.pricePerKg)} / kg", "${formatRub(cake.pricePerKg)} / кг")

@Composable
fun StockBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.background(color, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** Small horizontal strip used for "Recently viewed" and "Favorites". */
@Composable
fun CakeStrip(cakes: List<Cake>, onCakeClick: (String) -> Unit) {
    val en = LocalEnglish.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cakes, key = { it.id }) { cake ->
            Column(Modifier.width(120.dp).clickable { onCakeClick(cake.id) }) {
                CakeImage(cake, Modifier.size(120.dp).clip(RoundedCornerShape(14.dp)))
                Spacer(Modifier.height(6.dp))
                Text(cake.name(en), color = Mas.colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(priceLabel(cake), color = Mas.colors.accent, fontSize = 12.sp)
            }
        }
    }
}

// ------------------------------------------------------------------ shimmer

@Composable
fun shimmerBrush(): Brush {
    val base = Mas.colors.shimmer
    val highlight = if (Mas.colors.isDark) Color(0xFF4A3830) else Color(0xFFF8F3EC)
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -400f, targetValue = 1200f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX"
    )
    return Brush.linearGradient(listOf(base, highlight, base), start = Offset(x, 0f), end = Offset(x + 400f, 400f))
}

@Composable
fun ShimmerBox(modifier: Modifier, corner: Dp = 10.dp) {
    Box(modifier.clip(RoundedCornerShape(corner)).background(shimmerBrush()))
}

/** Grey placeholder rows shown for 1 second while "fetching" cakes. */
@Composable
fun ShimmerCakeList(rows: Int = 5, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(rows) {
            Row(
                Modifier.fillMaxWidth().background(Mas.colors.surface, RoundedCornerShape(14.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(Modifier.size(84.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    ShimmerBox(Modifier.fillMaxWidth(0.8f).height(16.dp), 6.dp)
                    Spacer(Modifier.height(10.dp))
                    ShimmerBox(Modifier.fillMaxWidth(0.4f).height(14.dp), 6.dp)
                }
            }
        }
    }
}

// ------------------------------------------------------------------ lottie

/**
 * Plays a Lottie animation from res/raw. If the file can't be loaded,
 * the [fallback] is shown instead so the screen never looks empty.
 */
@Composable
fun LottieOrFallback(
    @RawRes res: Int,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    fallback: @Composable () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(res))
    Box(modifier, contentAlignment = Alignment.Center) {
        if (composition == null) fallback()
        else LottieAnimation(
            composition = composition,
            iterations = if (loop) LottieConstants.IterateForever else 1,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ------------------------------------------------------------------ empty state

/** Hand-drawn empty plate with fork and knife. */
@Composable
fun EmptyPlateIllustration(modifier: Modifier = Modifier.size(180.dp)) {
    val accent = Mas.colors.accent
    val plate = if (Mas.colors.isDark) Color(0xFF3A2C26) else Color.White
    val rim = if (Mas.colors.isDark) Color(0xFF4F3D34) else Color(0xFFF1E6D8)
    val cutlery = Mas.colors.textMuted
    Canvas(modifier) {
        val c = center
        val r = size.minDimension * 0.34f
        drawCircle(accent.copy(alpha = 0.15f), radius = r * 1.35f, center = c)
        drawCircle(plate, radius = r, center = c)
        drawCircle(rim, radius = r * 0.72f, center = c, style = Stroke(width = r * 0.06f))
        drawCircle(accent.copy(alpha = 0.35f), radius = r * 0.08f, center = Offset(c.x + r * 0.25f, c.y - r * 0.2f))
        val stroke = r * 0.07f
        // fork
        val fx = c.x - r * 1.22f
        drawLine(cutlery, Offset(fx, c.y - r * 0.8f), Offset(fx, c.y + r * 0.9f), stroke, StrokeCap.Round)
        listOf(-0.12f, 0f, 0.12f).forEach { dx ->
            drawLine(cutlery, Offset(fx + r * dx, c.y - r * 0.8f), Offset(fx + r * dx, c.y - r * 0.45f), stroke * 0.6f, StrokeCap.Round)
        }
        // knife
        val kx = c.x + r * 1.22f
        drawLine(cutlery, Offset(kx, c.y - r * 0.1f), Offset(kx, c.y + r * 0.9f), stroke, StrokeCap.Round)
        drawRoundRect(cutlery, topLeft = Offset(kx - stroke * 0.9f, c.y - r * 0.8f), size = Size(stroke * 1.8f, r * 0.75f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke, stroke))
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, buttonText: String? = null, onButton: () -> Unit = {}) {
    Column(
        Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyPlateIllustration()
        Spacer(Modifier.height(16.dp))
        Text(title, color = Mas.colors.text, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = Mas.colors.textMuted, fontSize = 15.sp, textAlign = TextAlign.Center)
        if (buttonText != null) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(buttonText, onClick = onButton, modifier = Modifier.fillMaxWidth(0.7f))
        }
    }
}

// ------------------------------------------------------------------ buttons & bars

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, height: Dp = 55.dp) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Mas.colors.primary, contentColor = Mas.colors.onPrimary)
    ) {
        Text(text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, color = Mas.colors.text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Back", "Назад"), tint = Mas.colors.text)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Mas.colors.background)
    )
}

@Composable
fun LanguageToggle(onToggle: () -> Unit) {
    TextButton(onClick = onToggle) {
        Text(
            text = if (LocalEnglish.current) "RU 🇷🇺" else "EN 🇬🇧",
            color = Mas.colors.text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun masChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Mas.colors.surface,
    labelColor = Mas.colors.text,
    selectedContainerColor = Mas.colors.primary,
    selectedLabelColor = Mas.colors.onPrimary,
    selectedLeadingIconColor = Mas.colors.onPrimary
)

@Composable
fun masTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Mas.colors.surface,
    unfocusedContainerColor = Mas.colors.surface,
    focusedBorderColor = Mas.colors.primary,
    unfocusedBorderColor = Mas.colors.divider,
    focusedLabelColor = Mas.colors.primary,
    cursorColor = Mas.colors.primary,
    focusedTextColor = Mas.colors.text,
    unfocusedTextColor = Mas.colors.text
)

// ------------------------------------------------------------------ prices

@Composable
fun PriceBreakdown(subtotal: Int, delivery: Int, discount: Int, isDelivery: Boolean = true) {
    val pct = AppStore.settings.discountPercent
    Card(
        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BreakdownRow(tr("Subtotal", "Сумма"), formatRub(subtotal))
            BreakdownRow(
                tr("Delivery", "Доставка"),
                when {
                    !isDelivery -> tr("Pickup", "Самовывоз")
                    delivery == 0 && subtotal > 0 -> tr("Free", "Бесплатно")
                    else -> formatRub(delivery)
                }
            )
            if (discount > 0) BreakdownRow(tr("Discount ($pct%)", "Скидка ($pct%)"), "-" + formatRub(discount), valueColor = Strawberry)
            HorizontalDivider(color = Mas.colors.divider)
            BreakdownRow(tr("Total", "Итого"), formatRub(subtotal + delivery - discount), bold = true)
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String, bold: Boolean = false, valueColor: Color = Mas.colors.text) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = if (bold) Mas.colors.text else Mas.colors.textMuted, fontSize = if (bold) 18.sp else 15.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
        Text(value, color = valueColor, fontSize = if (bold) 18.sp else 15.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold)
    }
}

// ------------------------------------------------------------------ orders

fun statusColor(status: OrderStatus): Color = when (status) {
    OrderStatus.NEW -> Blueberry
    OrderStatus.PREPARING -> Butter
    OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFD4A373)
    OrderStatus.COMPLETED -> Pistachio
    OrderStatus.CANCELLED -> Strawberry
}

@Composable
fun StatusChip(status: OrderStatus) {
    val color = statusColor(status)
    Text(
        status.title(LocalEnglish.current),
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.background(color.copy(alpha = 0.15f), RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun InfoRow(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Mas.colors.accent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = Mas.colors.text, fontSize = 15.sp)
    }
}

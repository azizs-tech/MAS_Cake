package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.WEIGHT_OPTIONS
import com.example.mas_cake1.data.formatKg
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.CakeImage
import com.example.mas_cake1.ui.components.EmptyState
import com.example.mas_cake1.ui.components.PriceBreakdown
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.components.masChipColors
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onCheckoutClicked: () -> Unit, onBrowse: () -> Unit) {
    val en = LocalEnglish.current
    val cart = AppStore.cart
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    if (cart.isEmpty()) {
        // Empty state illustration (feature 7)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(
                tr("Your cart is empty", "Ваша корзина пуста"),
                tr("Let's add something sweet!", "Давайте добавим что-нибудь сладкое!"),
                buttonText = tr("Browse cakes", "Смотреть торты"),
                onButton = onBrowse
            )
        }
        return
    }

    val subtotal = AppStore.cartSubtotal()
    val discount = AppStore.cartDiscount()
    val delivery = AppStore.deliveryFeeFor(subtotal, isDelivery = true)

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(tr("Your Order", "Ваш заказ"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text(tr("Tap an item to change it", "Нажмите на товар, чтобы изменить"), fontSize = 13.sp, color = Mas.colors.textMuted, modifier = Modifier.padding(bottom = 12.dp))

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cart.size) { index ->
                val item = cart[index]
                val cake = AppStore.cake(item.cakeId)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().clickable { editingIndex = index }
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CakeImage(cake, Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cake?.name(en) ?: item.cakeId, color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2)
                            Text(
                                (if (cake?.isPerPiece == true) tr("1 piece", "1 шт") else formatKg(item.weightKg, en)) + " · ×${item.quantity}",
                                color = Mas.colors.textMuted, fontSize = 13.sp
                            )
                            Text(formatRub(AppStore.linePrice(item)), color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        QuantityStepper(
                            quantity = item.quantity,
                            onMinus = { AppStore.updateCartItem(index, item.weightKg, item.quantity - 1) },
                            onPlus = { AppStore.updateCartItem(index, item.weightKg, item.quantity + 1) }
                        )
                    }
                }
            }
            // Discount breakdown (feature 6)
            item { PriceBreakdown(subtotal, delivery, discount) }
            item {
                if (delivery > 0) Text(
                    tr("Free delivery from ${formatRub(AppStore.settings.freeDeliveryFrom)}", "Бесплатная доставка от ${formatRub(AppStore.settings.freeDeliveryFrom)}"),
                    color = Mas.colors.textMuted, fontSize = 13.sp
                )
            }
        }

        PrimaryButton(tr("Proceed to Checkout", "Перейти к оформлению"), onClick = onCheckoutClicked, modifier = Modifier.padding(vertical = 12.dp))
    }

    // Bottom sheet editor instead of a dialog (feature 4)
    editingIndex?.let { index ->
        val item = cart.getOrNull(index)
        val cake = item?.let { AppStore.cake(it.cakeId) }
        if (item == null || cake == null) {
            editingIndex = null
            return@let
        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        var weight by remember(index) { mutableDoubleStateOf(item.weightKg) }
        var quantity by remember(index) { mutableIntStateOf(item.quantity) }
        fun close() { scope.launch { sheetState.hide() }.invokeOnCompletion { editingIndex = null } }

        ModalBottomSheet(
            onDismissRequest = { editingIndex = null },
            sheetState = sheetState,
            containerColor = Mas.colors.surface
        ) {
            Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CakeImage(cake, Modifier.size(72.dp).clip(RoundedCornerShape(14.dp)))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(cake.name(en), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                        Text(formatRub(cake.priceFor(weight) * quantity), fontSize = 18.sp, color = Mas.colors.accent, fontWeight = FontWeight.Bold)
                    }
                }
                if (!cake.isPerPiece) {
                    Spacer(Modifier.height(20.dp))
                    Text(tr("Weight", "Вес"), color = Mas.colors.text, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WEIGHT_OPTIONS.forEach { option ->
                            FilterChip(selected = weight == option, onClick = { weight = option }, label = { Text(formatKg(option, en)) }, colors = masChipColors())
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr("Quantity", "Количество"), color = Mas.colors.text, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    QuantityStepper(quantity, onMinus = { if (quantity > 1) quantity-- }, onPlus = { quantity++ })
                }
                Spacer(Modifier.height(24.dp))
                PrimaryButton(tr("Save changes", "Сохранить"), onClick = {
                    AppStore.updateCartItem(index, weight, quantity)
                    close()
                })
                TextButton(
                    onClick = { AppStore.removeCartItem(index); close() },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, null, tint = Strawberry)
                    Spacer(Modifier.width(8.dp))
                    Text(tr("Remove from cart", "Удалить из корзины"), color = Strawberry)
                }
            }
        }
    }
}

@Composable
fun QuantityStepper(quantity: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(onClick = onMinus, modifier = Modifier.size(32.dp), shape = CircleShape) {
            Icon(Icons.Default.Remove, contentDescription = tr("Decrease", "Меньше"), modifier = Modifier.size(18.dp))
        }
        Text("$quantity", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.padding(horizontal = 12.dp))
        FilledTonalIconButton(onClick = onPlus, modifier = Modifier.size(32.dp), shape = CircleShape) {
            Icon(Icons.Default.Add, contentDescription = tr("Increase", "Больше"), modifier = Modifier.size(18.dp))
        }
    }
}

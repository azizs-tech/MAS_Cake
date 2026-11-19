package com.example.mas_cake1.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Order
import com.example.mas_cake1.data.OrderStatus
import com.example.mas_cake1.data.formatKg
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.*
import com.example.mas_cake1.ui.theme.Butter
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class HistoryStatus { ALL, COMPLETED, CANCELLED }
private enum class HistoryPeriod { TODAY, WEEK, ALL }

// ------------------------------------------------------------------ Orders list (features 5, 6, 8)

@Composable
fun AdminOrdersContent(onOpenOrder: (Int) -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var historyStatus by remember { mutableStateOf(HistoryStatus.ALL) }
    var historyPeriod by remember { mutableStateOf(HistoryPeriod.WEEK) }

    val active = AppStore.orders.filter { it.status.isActive }.sortedWith(compareBy({ it.status.ordinal }, { -it.createdAt }))
    val history = AppStore.orders.filter { !it.status.isActive }
        .filter {
            when (historyStatus) {
                HistoryStatus.ALL -> true
                HistoryStatus.COMPLETED -> it.status == OrderStatus.COMPLETED
                HistoryStatus.CANCELLED -> it.status == OrderStatus.CANCELLED
            }
        }
        .filter {
            when (historyPeriod) {
                HistoryPeriod.TODAY -> AppStore.isToday(it.createdAt)
                HistoryPeriod.WEEK -> it.createdAt >= AppStore.startOfDay(6)
                HistoryPeriod.ALL -> true
            }
        }

    Column(Modifier.fillMaxSize()) {
        Text(tr("Orders", "Заказы"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.padding(16.dp))
        TabRow(
            selectedTabIndex = tab,
            containerColor = Mas.colors.background,
            contentColor = Mas.colors.text
        ) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(tr("Live (${active.size})", "Активные (${active.size})")) })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(tr("History", "История")) })
        }

        if (tab == 0) {
            if (active.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(tr("No active orders", "Нет активных заказов"), tr("New orders will show up here.", "Новые заказы появятся здесь."))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(active, key = { it.id }) { order ->
                        OrderCard(order, onClick = { onOpenOrder(order.id) }, showNextButton = true)
                    }
                }
            }
        } else {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(historyStatus == HistoryStatus.ALL, { historyStatus = HistoryStatus.ALL }, { Text(tr("All", "Все")) }, colors = masChipColors()) }
                item { FilterChip(historyStatus == HistoryStatus.COMPLETED, { historyStatus = HistoryStatus.COMPLETED }, { Text(tr("Delivered", "Доставлены")) }, colors = masChipColors()) }
                item { FilterChip(historyStatus == HistoryStatus.CANCELLED, { historyStatus = HistoryStatus.CANCELLED }, { Text(tr("Cancelled", "Отменены")) }, colors = masChipColors()) }
            }
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(historyPeriod == HistoryPeriod.TODAY, { historyPeriod = HistoryPeriod.TODAY }, { Text(tr("Today", "Сегодня")) }, colors = masChipColors()) }
                item { FilterChip(historyPeriod == HistoryPeriod.WEEK, { historyPeriod = HistoryPeriod.WEEK }, { Text(tr("Last 7 days", "За неделю")) }, colors = masChipColors()) }
                item { FilterChip(historyPeriod == HistoryPeriod.ALL, { historyPeriod = HistoryPeriod.ALL }, { Text(tr("All time", "Всё время")) }, colors = masChipColors()) }
            }
            if (history.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(tr("Nothing here", "Пусто"), tr("No orders match these filters.", "Нет заказов по этим фильтрам."))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(history, key = { it.id }) { order -> OrderCard(order, onClick = { onOpenOrder(order.id) }) }
                }
            }
        }
    }
}

/** One order in a list: number, customer, items, total and a "move to next stage" button. */
@Composable
fun OrderCard(order: Order, onClick: () -> Unit, showNextButton: Boolean = false) {
    val en = LocalEnglish.current
    val time = remember(order.createdAt) { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(order.createdAt)) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#${order.id}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Mas.colors.text)
                Spacer(Modifier.width(10.dp))
                Text(time, color = Mas.colors.textMuted, fontSize = 13.sp, modifier = Modifier.weight(1f))
                StatusChip(order.status)
            }
            Spacer(Modifier.height(6.dp))
            Text(order.customerName, fontWeight = FontWeight.SemiBold, color = Mas.colors.text)
            Text(
                order.items.joinToString { (AppStore.cake(it.cakeId)?.name(en) ?: it.name) + " ×${it.quantity}" },
                color = Mas.colors.textMuted, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            if (order.specialRequest.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("✍ " + order.specialRequest, color = Mas.colors.accent, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatRub(order.total), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Mas.colors.text, modifier = Modifier.weight(1f))
                val next = order.status.next
                if (showNextButton && next != null) {
                    Button(
                        onClick = { AppStore.setOrderStatus(order.id, next) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = statusColor(next), contentColor = androidx.compose.ui.graphics.Color.White)
                    ) {
                        Text(next.title(en), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Order details (feature 7)

@Composable
fun AdminOrderDetailScreen(orderId: Int, onBack: () -> Unit) {
    val en = LocalEnglish.current
    val context = LocalContext.current
    val order = AppStore.order(orderId)
    var confirmCancel by remember { mutableStateOf(false) }

    Scaffold(topBar = { BackTopBar(tr("Order #$orderId", "Заказ №$orderId"), onBack) }, containerColor = Mas.colors.background) { padding ->
        if (order == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(tr("Order not found", "Заказ не найден"), "")
            }
            return@Scaffold
        }
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Status stepper
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tr("Status", "Статус"), fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.weight(1f))
                        StatusChip(order.status)
                    }
                    Spacer(Modifier.height(14.dp))
                    val steps = listOf(OrderStatus.NEW, OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY, OrderStatus.COMPLETED)
                    val currentIndex = steps.indexOf(order.status)
                    Row(Modifier.fillMaxWidth()) {
                        steps.forEachIndexed { i, s ->
                            val done = order.status != OrderStatus.CANCELLED && i <= currentIndex
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier.size(26.dp).background(if (done) statusColor(s) else Mas.colors.divider, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { if (done) Icon(Icons.Default.Check, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(16.dp)) }
                                Spacer(Modifier.height(4.dp))
                                Text(s.title(en), fontSize = 11.sp, color = if (done) Mas.colors.text else Mas.colors.textMuted, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    val next = order.status.next
                    if (next != null) {
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(tr("Move to: ${next.titleEn}", "Перевести в: ${next.titleRu}"), onClick = { AppStore.setOrderStatus(order.id, next) }, height = 50.dp)
                        TextButton(onClick = { confirmCancel = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(tr("Cancel order", "Отменить заказ"), color = Strawberry)
                        }
                    }
                }
            }

            // Customer
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(tr("Customer", "Клиент"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                    Spacer(Modifier.height(8.dp))
                    InfoRow(Icons.Default.Person, order.customerName)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InfoRow(Icons.Default.Phone, order.phone, Modifier.weight(1f))
                        TextButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.phone.filter { it.isDigit() || it == '+' })))
                        }) { Text(tr("Call", "Позвонить"), color = Mas.colors.accent) }
                    }
                    InfoRow(
                        if (order.isDelivery) Icons.Default.LocationOn else Icons.Default.Storefront,
                        if (order.isDelivery) order.address else tr("Pickup from bakery", "Самовывоз")
                    )
                    if (order.dateTime.isNotBlank()) InfoRow(Icons.Default.Schedule, order.dateTime)
                }
            }

            // Special request — highlighted so the baker doesn't miss it
            if (order.specialRequest.isNotBlank()) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Butter.copy(alpha = 0.18f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(tr("Special request", "Пожелание клиента"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                        Spacer(Modifier.height(6.dp))
                        Text("“${order.specialRequest}”", color = Mas.colors.text, fontSize = 16.sp)
                    }
                }
            }

            // Items
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tr("Items", "Состав заказа"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                    order.items.forEach { item ->
                        val cake = AppStore.cake(item.cakeId)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CakeImage(cake, Modifier.size(44.dp).background(Mas.colors.shimmer, RoundedCornerShape(8.dp)))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(cake?.name(en) ?: item.name, color = Mas.colors.text, fontWeight = FontWeight.SemiBold)
                                Text(
                                    (if (cake?.isPerPiece == true) tr("piece", "шт") else formatKg(item.weightKg, en)) + " × ${item.quantity}",
                                    color = Mas.colors.textMuted, fontSize = 13.sp
                                )
                            }
                            Text(formatRub(item.unitPrice * item.quantity), color = Mas.colors.text, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            PriceBreakdown(order.subtotal, order.deliveryFee, order.discount, order.isDelivery)
        }

        if (confirmCancel) {
            AlertDialog(
                onDismissRequest = { confirmCancel = false },
                title = { Text(tr("Cancel order #${order.id}?", "Отменить заказ №${order.id}?")) },
                text = { Text(tr("The customer will need to place a new order.", "Клиенту придётся оформить заказ заново.")) },
                confirmButton = {
                    TextButton(onClick = { AppStore.setOrderStatus(order.id, OrderStatus.CANCELLED); confirmCancel = false }) {
                        Text(tr("Cancel order", "Отменить"), color = Strawberry)
                    }
                },
                dismissButton = { TextButton(onClick = { confirmCancel = false }) { Text(tr("Keep", "Оставить")) } },
                containerColor = Mas.colors.surface
            )
        }
    }
}

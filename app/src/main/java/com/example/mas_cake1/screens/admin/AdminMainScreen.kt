package com.example.mas_cake1.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.OrderStatus
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.SectionTitle
import com.example.mas_cake1.ui.components.statusColor
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.LightCaramel
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Pistachio
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr

@Composable
fun AdminMainScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onOpenOrder: (Int) -> Unit,
    onEditCake: (String) -> Unit,
    onAddCake: () -> Unit,
    onOpenPromo: () -> Unit,
    onOpenCustomers: () -> Unit,
    onExit: () -> Unit
) {
    val tabs = listOf(
        tr("Dashboard", "Главная") to Icons.Outlined.Dashboard,
        tr("Orders", "Заказы") to Icons.Outlined.Receipt,
        tr("Cakes", "Торты") to Icons.Outlined.Cake,
        tr("Analytics", "Аналитика") to Icons.Outlined.Insights,
        tr("Settings", "Настройки") to Icons.Outlined.Settings
    )
    val newOrders = AppStore.countByStatus(OrderStatus.NEW)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Mas.colors.surface) {
                tabs.forEachIndexed { index, (title, icon) ->
                    NavigationBarItem(
                        icon = {
                            if (index == 1 && newOrders > 0) BadgedBox(badge = { Badge { Text("$newOrders") } }) { Icon(icon, title) }
                            else Icon(icon, title)
                        },
                        label = { Text(title, fontSize = 11.sp) },
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Mas.colors.text,
                            selectedTextColor = Mas.colors.text,
                            indicatorColor = Mas.colors.accent.copy(alpha = 0.25f),
                            unselectedIconColor = Mas.colors.textMuted,
                            unselectedTextColor = Mas.colors.textMuted
                        )
                    )
                }
            }
        },
        containerColor = Mas.colors.background
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> AdminDashboard(
                    onOpenOrders = { onTabSelected(1) },
                    onOpenOrder = onOpenOrder,
                    onAddCake = onAddCake,
                    onEditCake = onEditCake,
                    onOpenPromo = onOpenPromo,
                    onOpenCustomers = onOpenCustomers,
                    onExit = onExit
                )
                1 -> AdminOrdersContent(onOpenOrder)
                2 -> AdminCakesContent(onEditCake = onEditCake, onAddCake = onAddCake, onOpenPromo = onOpenPromo)
                3 -> AdminAnalyticsContent(onOpenCustomers)
                4 -> AdminSettingsContent(onOpenPromo = onOpenPromo, onExit = onExit)
            }
        }
    }
}

// ------------------------------------------------------------------ Dashboard (features 1–4)

@Composable
fun AdminDashboard(
    onOpenOrders: () -> Unit,
    onOpenOrder: (Int) -> Unit,
    onAddCake: () -> Unit,
    onEditCake: (String) -> Unit,
    onOpenPromo: () -> Unit,
    onOpenCustomers: () -> Unit,
    onExit: () -> Unit
) {
    val en = LocalEnglish.current
    val revenue = AppStore.todayRevenue()
    val todayCount = AppStore.orders.count { AppStore.isToday(it.createdAt) && it.status != OrderStatus.CANCELLED }
    val alerts = AppStore.cakes.filter { !it.canOrder || it.isLowStock }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tr("Admin panel", "Панель управления"), fontSize = 14.sp, color = Mas.colors.textMuted)
                    Text("MAS Cake", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Mas.colors.text)
                }
                IconButton(onClick = onExit) { Icon(Icons.AutoMirrored.Filled.ExitToApp, tr("Exit", "Выйти"), tint = Mas.colors.text) }
            }
        }

        // 1. Today's revenue
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = ChocolateBrown)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(tr("Today's revenue", "Выручка за сегодня"), color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)
                    Text(formatRub(revenue), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                    Text(tr("$todayCount orders today", "Заказов сегодня: $todayCount"), color = LightCaramel, fontSize = 14.sp)
                }
            }
        }

        // 2. Orders counter
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(OrderStatus.NEW, OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY).forEach { status ->
                    Card(
                        modifier = Modifier.weight(1f).clickable(onClick = onOpenOrders),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Box(Modifier.size(10.dp).background(statusColor(status), CircleShape))
                            Spacer(Modifier.height(8.dp))
                            Text("${AppStore.countByStatus(status)}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                            Text(status.title(en), fontSize = 13.sp, color = Mas.colors.textMuted, maxLines = 1)
                        }
                    }
                }
            }
        }

        // 4. Quick actions
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction(Icons.Default.Add, tr("Add new cake", "Добавить торт"), Modifier.weight(1f), onAddCake)
                QuickAction(Icons.Default.ListAlt, tr("View all orders", "Все заказы"), Modifier.weight(1f), onOpenOrders)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction(Icons.Default.Percent, tr("Promo & discount", "Акции и скидки"), Modifier.weight(1f), onOpenPromo)
                QuickAction(Icons.Default.People, tr("Customers", "Клиенты"), Modifier.weight(1f), onOpenCustomers)
            }
        }

        // 3. Low stock / out of stock alerts
        item {
            SectionTitle(tr("Stock alerts", "Остатки"))
        }
        if (alerts.isEmpty()) {
            item { Text(tr("Everything is in stock 👍", "Всё в наличии 👍"), color = Mas.colors.textMuted) }
        } else {
            items(alerts.size) { i ->
                val cake = alerts[i]
                val out = !cake.canOrder
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onEditCake(cake.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = (if (out) Strawberry else LightCaramel).copy(alpha = 0.12f))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = if (out) Strawberry else LightCaramel)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cake.name(en), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                            Text(
                                if (out) tr("Out of stock — hidden from ordering", "Нет в наличии — заказ недоступен")
                                else tr("Running low: ${cake.stock} left", "Заканчивается: осталось ${cake.stock}"),
                                fontSize = 13.sp, color = Mas.colors.textMuted
                            )
                        }
                        Icon(Icons.Default.Edit, tr("Edit", "Изменить"), tint = Mas.colors.textMuted)
                    }
                }
            }
        }

        // Latest orders preview
        item { SectionTitle(tr("Latest orders", "Последние заказы")) { TextButton(onClick = onOpenOrders) { Text(tr("See all", "Все"), color = Mas.colors.accent) } } }
        val latest = AppStore.orders.filter { it.status.isActive }.take(3)
        items(latest.size) { i -> OrderCard(latest[i], onClick = { onOpenOrder(latest[i].id) }) }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(84.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Mas.colors.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.size(30.dp).background(Pistachio.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Pistachio, modifier = Modifier.size(18.dp))
            }
            Text(label, fontWeight = FontWeight.Bold, color = Mas.colors.text, fontSize = 14.sp)
        }
    }
}

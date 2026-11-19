package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.formatKg
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.*
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ------------------------------------------------------------------ Edit profile (feature 10)

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val settings = AppStore.settings
    var name by remember { mutableStateOf(settings.userName) }
    var phone by remember { mutableStateOf(settings.userPhone) }
    var email by remember { mutableStateOf(settings.userEmail) }

    Scaffold(topBar = { BackTopBar(tr("Edit profile", "Редактировать профиль"), onBack) }, containerColor = Mas.colors.background) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).imePadding()) {
            listOf(
                Triple(name, tr("Name", "Имя"), KeyboardType.Text),
                Triple(phone, tr("Phone number", "Номер телефона"), KeyboardType.Phone),
                Triple(email, tr("Email", "Эл. почта"), KeyboardType.Email)
            ).forEachIndexed { i, (value, label, kb) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { v -> when (i) { 0 -> name = v; 1 -> phone = v; else -> email = v } },
                    label = { Text(label) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = kb),
                    shape = RoundedCornerShape(12.dp),
                    colors = masTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            PrimaryButton(tr("Save", "Сохранить"), enabled = name.isNotBlank(), onClick = {
                AppStore.updateSettings { it.copy(userName = name.trim(), userPhone = phone.trim(), userEmail = email.trim()) }
                onBack()
            })
        }
    }
}

// ------------------------------------------------------------------ Favorites (feature 8)

@Composable
fun FavoritesScreen(onBack: () -> Unit, onOpenCake: (String) -> Unit) {
    val cakes = AppStore.favorites.mapNotNull { AppStore.cake(it) }
    Scaffold(topBar = { BackTopBar(tr("Favorites", "Избранное"), onBack) }, containerColor = Mas.colors.background) { padding ->
        if (cakes.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    tr("No favorites yet", "Пока нет избранного"),
                    tr("Tap the ♡ on any cake to save it here.", "Нажмите ♡ на любом торте, чтобы сохранить его здесь.")
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cakes, key = { it.id }) { cake -> CakeGridCard(cake, onClick = { onOpenCake(cake.id) }) }
            }
        }
    }
}

// ------------------------------------------------------------------ My orders

@Composable
fun MyOrdersScreen(onBack: () -> Unit) {
    val en = LocalEnglish.current
    val phone = AppStore.settings.userPhone
    val myOrders = AppStore.orders.filter { phone.isNotBlank() && it.phone == phone }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(topBar = { BackTopBar(tr("My orders", "Мои заказы"), onBack) }, containerColor = Mas.colors.background) { padding ->
        if (myOrders.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(tr("No orders yet", "Заказов пока нет"), tr("Your orders will appear here.", "Здесь появятся ваши заказы."))
            }
        } else {
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(myOrders, key = { it.id }) { order ->
                    Card(colors = CardDefaults.cardColors(containerColor = Mas.colors.surface), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tr("Order #${order.id}", "Заказ №${order.id}"), fontWeight = FontWeight.Bold, color = Mas.colors.text, fontSize = 17.sp, modifier = Modifier.weight(1f))
                                StatusChip(order.status)
                            }
                            Text(dateFormat.format(Date(order.createdAt)), color = Mas.colors.textMuted, fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            order.items.forEach { item ->
                                val title = AppStore.cake(item.cakeId)?.name(en) ?: item.name
                                Text("• $title, ${formatKg(item.weightKg, en)} ×${item.quantity}", color = Mas.colors.text, fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(formatRub(order.total), fontWeight = FontWeight.Bold, color = Mas.colors.accent)
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Settings (feature 13: dark mode)

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val settings = AppStore.settings
    Scaffold(topBar = { BackTopBar(tr("Settings", "Настройки"), onBack) }, containerColor = Mas.colors.background) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            SettingSwitch(Icons.Default.DarkMode, tr("Dark mode", "Тёмная тема"), tr("Dark brown / black look", "Тёмно-коричневое оформление"), settings.darkMode) { v ->
                AppStore.updateSettings { it.copy(darkMode = v) }
            }
            SettingSwitch(Icons.Default.Language, tr("English language", "Английский язык"), tr("Switch off for Russian", "Выключите для русского"), settings.isEnglish) { v ->
                AppStore.updateSettings { it.copy(isEnglish = v) }
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, null, tint = Mas.colors.accent)
                Spacer(Modifier.width(16.dp))
                Text(
                    tr("Order notifications are sent 10 seconds after you place an order.", "Уведомление о заказе приходит через 10 секунд после оформления."),
                    color = Mas.colors.textMuted, fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SettingSwitch(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Mas.colors.accent)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Mas.colors.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Mas.colors.textMuted, fontSize = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Mas.colors.primary, checkedThumbColor = Mas.colors.onPrimary)
        )
    }
    HorizontalDivider(color = Mas.colors.divider)
}

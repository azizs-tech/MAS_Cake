package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun ProfileScreen(
    onEditProfile: () -> Unit,
    onFavorites: () -> Unit,
    onMyOrders: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val settings = AppStore.settings
    var infoDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    val name = settings.userName.ifBlank { tr("Guest", "Гость") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(tr("My profile", "Мой профиль"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(64.dp).background(Mas.colors.accent, CircleShape), contentAlignment = Alignment.Center) {
                Text(name.take(1).uppercase(), color = Mas.colors.onPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                Text(settings.userPhone.ifBlank { tr("No phone yet", "Телефон не указан") }, fontSize = 15.sp, color = Mas.colors.textMuted)
            }
            IconButton(onClick = onEditProfile) { Icon(Icons.Default.Edit, contentDescription = tr("Edit profile", "Редактировать"), tint = Mas.colors.text) }
        }

        Spacer(Modifier.height(24.dp))

        val favCount = AppStore.favorites.size
        ProfileMenuItem(Icons.Default.Refresh, tr("My orders", "Мои заказы"), onClick = onMyOrders)
        ProfileMenuItem(Icons.Default.FavoriteBorder, tr("Favorites", "Избранное") + if (favCount > 0) " ($favCount)" else "", onClick = onFavorites)
        // Texts are prepared here because tr() can only be used while drawing, not inside a click handler.
        val deliveryTitle = tr("Delivery terms", "Условия доставки")
        val deliveryBody = tr(
            "Courier delivery: ${formatRub(settings.deliveryFee)}\nFree delivery from ${formatRub(settings.freeDeliveryFrom)}\nPickup from the bakery is always free.",
            "Доставка курьером: ${formatRub(settings.deliveryFee)}\nБесплатно от ${formatRub(settings.freeDeliveryFrom)}\nСамовывоз из кондитерской — всегда бесплатно."
        )
        val contactsTitle = tr("Contacts", "Контакты")
        val contactsBody = tr(
            "Phone: ${settings.contactPhone}\nOpen every day ${settings.openTime}–${settings.closeTime}",
            "Телефон: ${settings.contactPhone}\nРаботаем ежедневно ${settings.openTime}–${settings.closeTime}"
        )
        ProfileMenuItem(Icons.Default.DirectionsCar, deliveryTitle) { infoDialog = deliveryTitle to deliveryBody }
        ProfileMenuItem(Icons.Default.Call, contactsTitle) { infoDialog = contactsTitle to contactsBody }
        ProfileMenuItem(Icons.Default.Settings, tr("Settings", "Настройки"), onClick = onSettings)

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onLogout).padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Mas.colors.text)
            Spacer(Modifier.width(16.dp))
            Text(tr("Log out", "Выйти из профиля"), color = Mas.colors.text, fontSize = 16.sp)
        }
    }

    infoDialog?.let { (title, body) ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            confirmButton = { TextButton(onClick = { infoDialog = null }) { Text("OK", color = Mas.colors.primary) } },
            title = { Text(title) },
            text = { Text(body) },
            containerColor = Mas.colors.surface
        )
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Mas.colors.accent)
        Spacer(Modifier.width(16.dp))
        Text(title, color = Mas.colors.text, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Mas.colors.textMuted)
    }
    HorizontalDivider(color = Mas.colors.divider)
}

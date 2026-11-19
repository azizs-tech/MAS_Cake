package com.example.mas_cake1.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.*
import com.example.mas_cake1.ui.screens.SettingSwitch
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ------------------------------------------------------------------ Business analytics (feature 14)

@Composable
fun AdminAnalyticsContent(onOpenCustomers: () -> Unit) {
    val en = LocalEnglish.current
    val best = AppStore.bestSellers().take(5)
    val days = AppStore.revenueLast7Days()
    val weekTotal = days.sumOf { it.second }
    val dayFormat = remember(en) { SimpleDateFormat("EE", if (en) Locale.ENGLISH else Locale("ru")) }
    val topName = best.firstOrNull()?.let { AppStore.cake(it.first)?.name(en) }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text(tr("Analytics", "Аналитика"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text) }

        if (topName != null) item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.accent.copy(alpha = 0.18f))) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 36.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(tr("#1 seller", "Хит №1"), color = Mas.colors.textMuted, fontSize = 13.sp)
                        Text(tr("$topName is the #1 seller", "$topName — самый продаваемый"), color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        // Best-selling cakes – horizontal bars
        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(tr("Best-selling cakes (pieces)", "Самые продаваемые (шт)"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                    val max = (best.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                    best.forEachIndexed { i, (id, count) ->
                        Column {
                            Row {
                                Text("${i + 1}. " + (AppStore.cake(id)?.name(en) ?: id), color = Mas.colors.text, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("$count", color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Mas.colors.divider)) {
                                Box(
                                    Modifier.fillMaxWidth(count / max.toFloat()).fillMaxHeight().clip(CircleShape)
                                        .background(if (i == 0) Mas.colors.primary else Mas.colors.accent)
                                )
                            }
                        }
                    }
                    if (best.isEmpty()) Text(tr("No sales yet", "Продаж пока нет"), color = Mas.colors.textMuted)
                }
            }
        }

        // Revenue for the last 7 days – vertical bars
        item {
            val barColor = Mas.colors.accent
            val todayColor = Mas.colors.primary
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(tr("Revenue, last 7 days", "Выручка за 7 дней"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                    Text(formatRub(weekTotal), color = Mas.colors.text, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(12.dp))
                    val max = (days.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
                        val slot = size.width / days.size
                        val barWidth = slot * 0.55f
                        days.forEachIndexed { i, (_, value) ->
                            val h = (value / max.toFloat()) * size.height
                            drawRoundRect(
                                color = if (i == days.lastIndex) todayColor else barColor,
                                topLeft = Offset(i * slot + (slot - barWidth) / 2, size.height - h),
                                size = Size(barWidth, h.coerceAtLeast(4f)),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
                        days.forEach { (start, _) ->
                            Text(dayFormat.format(Date(start)), color = Mas.colors.textMuted, fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface), onClick = onOpenCustomers) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, tint = Mas.colors.accent)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tr("Customers", "Клиенты"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
                        Text(tr("${AppStore.customers().size} customers", "Клиентов: ${AppStore.customers().size}"), color = Mas.colors.textMuted, fontSize = 13.sp)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Mas.colors.textMuted)
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Customer list (feature 13)

@Composable
fun AdminCustomersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val customers = AppStore.customers()
    val smsText = tr(
        "Hi from MAS Cake! 🍰 ${AppStore.settings.discountPercent}% off selected cakes this week.",
        "Привет от MAS Cake! 🍰 Скидка ${AppStore.settings.discountPercent}% на избранные торты на этой неделе."
    )

    Scaffold(topBar = { BackTopBar(tr("Customers", "Клиенты"), onBack) }, containerColor = Mas.colors.background) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(customers, key = { it.phone }) { c ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).background(Mas.colors.accent.copy(alpha = 0.25f), CircleShape), contentAlignment = Alignment.Center) {
                            Text(c.name.take(1).uppercase(), color = Mas.colors.text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.name, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                            Text(c.phone, color = Mas.colors.textMuted, fontSize = 13.sp)
                            Text(
                                tr("${c.ordersCount} orders · ${formatRub(c.totalSpent)}", "Заказов: ${c.ordersCount} · ${formatRub(c.totalSpent)}"),
                                color = Mas.colors.accent, fontSize = 13.sp
                            )
                        }
                        // Opens the SMS app with a ready marketing message
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + c.phone.filter { it.isDigit() || it == '+' }))
                                .putExtra("sms_body", smsText)
                            runCatching { context.startActivity(intent) }
                        }) { Icon(Icons.Default.Sms, tr("Send SMS", "Отправить SMS"), tint = Mas.colors.text) }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Admin settings (feature 15)

@Composable
fun AdminSettingsContent(onOpenPromo: () -> Unit, onExit: () -> Unit) {
    val s = AppStore.settings
    var openTime by remember { mutableStateOf(s.openTime) }
    var closeTime by remember { mutableStateOf(s.closeTime) }
    var fee by remember { mutableStateOf(s.deliveryFee.toString()) }
    var freeFrom by remember { mutableStateOf(s.freeDeliveryFrom.toString()) }
    var phone by remember { mutableStateOf(s.contactPhone) }
    var saved by remember { mutableStateOf(false) }

    val timeOk = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")
    val valid = timeOk.matches(openTime) && timeOk.matches(closeTime) && fee.toIntOrNull() != null && freeFrom.toIntOrNull() != null && phone.isNotBlank()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).imePadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(tr("Settings", "Настройки"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)

        SectionTitle(tr("Working hours", "Часы работы"))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsField(openTime, { openTime = it; saved = false }, tr("Opens", "Открытие"), Modifier.weight(1f))
            SettingsField(closeTime, { closeTime = it; saved = false }, tr("Closes", "Закрытие"), Modifier.weight(1f))
        }

        SectionTitle(tr("Delivery", "Доставка"))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsField(fee, { fee = it.filter(Char::isDigit); saved = false }, tr("Delivery fee, ₽", "Доставка, ₽"), Modifier.weight(1f), KeyboardType.Number)
            SettingsField(freeFrom, { freeFrom = it.filter(Char::isDigit); saved = false }, tr("Free from, ₽", "Бесплатно от, ₽"), Modifier.weight(1f), KeyboardType.Number)
        }

        SectionTitle(tr("Contact", "Контакты"))
        SettingsField(phone, { phone = it; saved = false }, tr("Contact phone", "Телефон для связи"), Modifier.fillMaxWidth(), KeyboardType.Phone)

        PrimaryButton(if (saved) tr("Saved ✔", "Сохранено ✔") else tr("Save settings", "Сохранить"), enabled = valid && !saved, onClick = {
            AppStore.updateSettings {
                it.copy(openTime = openTime, closeTime = closeTime, deliveryFee = fee.toInt(), freeDeliveryFrom = freeFrom.toInt(), contactPhone = phone.trim())
            }
            saved = true
        })
        if (!valid) Text(tr("Use HH:MM for hours and numbers for prices.", "Часы в формате ЧЧ:ММ, цены — числами."), color = Mas.colors.textMuted, fontSize = 13.sp)

        HorizontalDivider(color = Mas.colors.divider, modifier = Modifier.padding(vertical = 8.dp))

        SettingSwitch(Icons.Default.DarkMode, tr("Dark mode", "Тёмная тема"), tr("Also applies to the customer app", "Применяется и к приложению клиента"), s.darkMode) { v ->
            AppStore.updateSettings { it.copy(darkMode = v) }
        }
        SettingSwitch(Icons.Default.Language, tr("English language", "Английский язык"), tr("Switch off for Russian", "Выключите для русского"), s.isEnglish) { v ->
            AppStore.updateSettings { it.copy(isEnglish = v) }
        }
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onOpenPromo) {
                Icon(Icons.Default.Percent, null, tint = Mas.colors.accent)
                Spacer(Modifier.width(8.dp))
                Text(tr("Promo & discount", "Акции и скидки"), color = Mas.colors.text)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onExit) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Mas.colors.text)
                Spacer(Modifier.width(8.dp))
                Text(tr("Exit admin", "Выйти"), color = Mas.colors.text)
            }
        }
    }
}

@Composable
private fun SettingsField(value: String, onChange: (String) -> Unit, label: String, modifier: Modifier, keyboard: KeyboardType = KeyboardType.Text) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(12.dp),
        colors = masTextFieldColors(),
        modifier = modifier
    )
}

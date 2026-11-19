package com.example.mas_cake1.ui.screens.admin

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Cake
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.data.CakeTag
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.*
import com.example.mas_cake1.ui.theme.LightCaramel
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.Strawberry
import com.example.mas_cake1.ui.theme.tr
import java.io.File

// ------------------------------------------------------------------ All cakes + availability toggle (features 9, 10)

@Composable
fun AdminCakesContent(onEditCake: (String) -> Unit, onAddCake: () -> Unit, onOpenPromo: () -> Unit) {
    val en = LocalEnglish.current
    var category by remember { mutableStateOf<CakeCategory?>(null) }
    val cakes = AppStore.cakes.filter { category == null || it.category == category }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(tr("Cakes", "Торты"), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
                        Text(
                            tr("${AppStore.cakes.count { it.canOrder }} of ${AppStore.cakes.size} available", "В наличии ${AppStore.cakes.count { it.canOrder }} из ${AppStore.cakes.size}"),
                            color = Mas.colors.textMuted, fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = onOpenPromo) { Icon(Icons.Default.Percent, tr("Promo", "Акции"), tint = Mas.colors.text) }
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(category == null, { category = null }, { Text(tr("All", "Все")) }, colors = masChipColors()) }
                    items(CakeCategory.entries) { c ->
                        FilterChip(category == c, { category = c }, { Text(c.title(en)) }, colors = masChipColors())
                    }
                }
            }
            items(cakes, key = { it.id }) { cake ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onEditCake(cake.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CakeImage(cake, Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)).alpha(if (cake.canOrder) 1f else 0.4f))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(cake.name(en), fontWeight = FontWeight.Bold, color = Mas.colors.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                "${cake.category.title(en)} · " + priceLabel(cake),
                                color = Mas.colors.textMuted, fontSize = 13.sp
                            )
                            Text(
                                when {
                                    !cake.canOrder -> tr("Out of stock", "Нет в наличии")
                                    cake.isLowStock -> tr("Low stock: ${cake.stock}", "Мало: ${cake.stock}")
                                    else -> tr("In stock: ${cake.stock}", "В наличии: ${cake.stock}")
                                },
                                color = if (!cake.canOrder || cake.isLowStock) Strawberry else Mas.colors.textMuted,
                                fontSize = 12.sp
                            )
                        }
                        // Instant "Out of stock" switch
                        Switch(
                            checked = cake.canOrder,
                            onCheckedChange = { AppStore.setAvailable(cake.id, it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = Mas.colors.primary, checkedThumbColor = Mas.colors.onPrimary)
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = onAddCake,
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text(tr("Add cake", "Добавить")) },
            containerColor = Mas.colors.primary,
            contentColor = Mas.colors.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

// ------------------------------------------------------------------ Add / Edit cake form (feature 11)

/** Copies the picked photo into the app's own storage so it keeps working after restarts. */
private fun copyImageToAppStorage(context: Context, uri: Uri): String? = try {
    val dir = File(context.filesDir, "cake_images").apply { mkdirs() }
    val file = File(dir, "cake_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input -> file.outputStream().use { input.copyTo(it) } }
    file.absolutePath
} catch (e: Exception) {
    null
}

@Composable
fun AdminCakeEditScreen(cakeId: String, onBack: () -> Unit) {
    val en = LocalEnglish.current
    val context = LocalContext.current
    val existing = remember(cakeId) { AppStore.cake(cakeId) }
    val isNew = existing == null

    var nameEn by remember { mutableStateOf(existing?.nameEn ?: "") }
    var nameRu by remember { mutableStateOf(existing?.nameRu ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: CakeCategory.BIRTHDAY) }
    var price by remember { mutableStateOf(existing?.pricePerKg?.toString() ?: "") }
    var stock by remember { mutableStateOf(existing?.stock?.toString() ?: "10") }
    var perPiece by remember { mutableStateOf(existing?.isPerPiece ?: false) }
    var descEn by remember { mutableStateOf(existing?.descEn ?: "") }
    var descRu by remember { mutableStateOf(existing?.descRu ?: "") }
    val tags = remember { mutableStateListOf<CakeTag>().apply { existing?.tags?.let { addAll(it) } } }
    var imagePath by remember { mutableStateOf(existing?.imagePath ?: "") }
    var confirmDelete by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) copyImageToAppStorage(context, uri)?.let { imagePath = it }
    }

    val priceInt = price.toIntOrNull()
    val canSave = nameEn.isNotBlank() && priceInt != null && priceInt > 0

    // Preview of the cake as it will look (uses the same image component as the customer app).
    val preview = (existing ?: Cake(id = "", nameEn = "", nameRu = "", category = category, pricePerKg = 0)).copy(imagePath = imagePath)

    Scaffold(
        topBar = {
            BackTopBar(if (isNew) tr("New cake", "Новый торт") else tr("Edit cake", "Редактирование"), onBack) {
                if (!isNew) IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Default.Delete, tr("Delete", "Удалить"), tint = Strawberry) }
            }
        },
        containerColor = Mas.colors.background
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image upload
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, Mas.colors.divider, RoundedCornerShape(18.dp))
                    .clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            ) {
                if (imagePath.isNotEmpty() || (existing?.imageKey?.isNotEmpty() == true)) {
                    CakeImage(preview, Modifier.fillMaxSize())
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)))))
                    Row(Modifier.align(Alignment.BottomStart).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(tr("Change photo", "Сменить фото"), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, null, tint = Mas.colors.accent, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(tr("Upload a photo", "Загрузить фото"), color = Mas.colors.text, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            FormField(nameEn, { nameEn = it }, tr("Name (English)", "Название (англ.)"))
            FormField(nameRu, { nameRu = it }, tr("Name (Russian)", "Название (рус.)"))

            Text(tr("Category", "Категория"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CakeCategory.entries.forEach { c ->
                    FilterChip(category == c, { category = c }, { Text(c.title(en)) }, colors = masChipColors())
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FormField(price, { price = it.filter(Char::isDigit) }, if (perPiece) tr("Price per piece, ₽", "Цена за шт, ₽") else tr("Price per kg, ₽", "Цена за кг, ₽"), Modifier.weight(1f), KeyboardType.Number)
                FormField(stock, { stock = it.filter(Char::isDigit) }, tr("In stock", "Остаток"), Modifier.weight(1f), KeyboardType.Number)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Sold per piece (no weight choice)", "Продаётся поштучно (без выбора веса)"), color = Mas.colors.text, modifier = Modifier.weight(1f))
                Switch(perPiece, { perPiece = it }, colors = SwitchDefaults.colors(checkedTrackColor = Mas.colors.primary, checkedThumbColor = Mas.colors.onPrimary))
            }

            Text(tr("Tags (used by search filters)", "Метки (для фильтров поиска)"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CakeTag.entries.forEach { t ->
                    val on = t in tags
                    FilterChip(on, { if (on) tags.remove(t) else tags.add(t) }, { Text(t.title(en)) }, colors = masChipColors())
                }
            }

            FormField(descEn, { descEn = it }, tr("Description (English)", "Описание (англ.)"), singleLine = false)
            FormField(descRu, { descRu = it }, tr("Description (Russian)", "Описание (рус.)"), singleLine = false)

            Spacer(Modifier.height(4.dp))
            PrimaryButton(tr("Save cake", "Сохранить торт"), enabled = canSave, onClick = {
                val stockInt = stock.toIntOrNull() ?: 0
                val base = existing ?: Cake(id = AppStore.newCakeId(), nameEn = "", nameRu = "", category = category, pricePerKg = 0)
                AppStore.upsertCake(
                    base.copy(
                        nameEn = nameEn.trim(),
                        nameRu = nameRu.trim().ifBlank { nameEn.trim() },
                        category = category,
                        pricePerKg = priceInt ?: 0,
                        stock = stockInt,
                        available = stockInt > 0 && (existing?.available ?: true),
                        isPerPiece = perPiece,
                        descEn = descEn.trim(),
                        descRu = descRu.trim(),
                        tags = tags.toSet(),
                        imagePath = imagePath
                    )
                )
                onBack()
            })
            if (priceInt == null && price.isNotEmpty()) Text(tr("Enter a valid price", "Введите цену"), color = Strawberry)
            Spacer(Modifier.height(12.dp))
        }
    }

    if (confirmDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(tr("Delete ${existing.name(en)}?", "Удалить «${existing.name(en)}»?")) },
            text = { Text(tr("Customers will no longer see this cake.", "Клиенты больше не увидят этот торт.")) },
            confirmButton = { TextButton(onClick = { AppStore.deleteCake(existing.id); confirmDelete = false; onBack() }) { Text(tr("Delete", "Удалить"), color = Strawberry) } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(tr("Cancel", "Отмена")) } },
            containerColor = Mas.colors.surface
        )
    }
}

@Composable
private fun FormField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboard: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(12.dp),
        colors = masTextFieldColors(),
        modifier = modifier
    )
}

// ------------------------------------------------------------------ Promo / discount manager (feature 12)

@Composable
fun AdminPromoScreen(onBack: () -> Unit) {
    val en = LocalEnglish.current
    val settings = AppStore.settings
    var percent by remember { mutableFloatStateOf(settings.discountPercent.toFloat()) }

    Scaffold(topBar = { BackTopBar(tr("Promo & discount", "Акции и скидки"), onBack) }, containerColor = Mas.colors.background) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Live preview of the home banner
            item {
                Box(
                    Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(LightCaramel, Color(0xFFB5835A))))
                        .padding(20.dp)
                ) {
                    Column(Modifier.align(Alignment.CenterStart)) {
                        Text(tr("Home banner preview", "Превью баннера"), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text(
                            if (settings.promoEnabled) tr("${percent.toInt()}% off cakes!", "Скидка ${percent.toInt()}% на торты!") else tr("Promo is off", "Акция выключена"),
                            color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text("🍰", fontSize = 52.sp, modifier = Modifier.align(Alignment.CenterEnd))
                }
            }
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Mas.colors.surface)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tr("Promotion active", "Акция включена"), fontWeight = FontWeight.Bold, color = Mas.colors.text, modifier = Modifier.weight(1f))
                            Switch(
                                checked = settings.promoEnabled,
                                onCheckedChange = { v -> AppStore.updateSettings { it.copy(promoEnabled = v) } },
                                colors = SwitchDefaults.colors(checkedTrackColor = Mas.colors.primary, checkedThumbColor = Mas.colors.onPrimary)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(tr("Discount: ${percent.toInt()}%", "Скидка: ${percent.toInt()}%"), color = Mas.colors.text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = percent,
                            onValueChange = { percent = it },
                            onValueChangeFinished = { AppStore.updateSettings { it.copy(discountPercent = percent.toInt()) } },
                            valueRange = 5f..50f,
                            steps = 8,
                            colors = SliderDefaults.colors(thumbColor = Mas.colors.primary, activeTrackColor = Mas.colors.primary)
                        )
                        Row { Text("5%", color = Mas.colors.textMuted, fontSize = 12.sp, modifier = Modifier.weight(1f)); Text("50%", color = Mas.colors.textMuted, fontSize = 12.sp) }
                    }
                }
            }
            item {
                SectionTitle(tr("Cakes in the promotion", "Торты в акции"))
                Text(tr("The discount only applies to these cakes.", "Скидка действует только на эти торты."), color = Mas.colors.textMuted, fontSize = 13.sp)
            }
            items(AppStore.cakes, key = { it.id }) { cake ->
                Row(
                    Modifier.fillMaxWidth().background(Mas.colors.surface, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CakeImage(cake, Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(cake.name(en), color = Mas.colors.text, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(formatRub(cake.pricePerKg), color = Mas.colors.textMuted, fontSize = 12.sp)
                    }
                    Checkbox(
                        checked = cake.onPromo,
                        onCheckedChange = { AppStore.setOnPromo(cake.id, it) },
                        colors = CheckboxDefaults.colors(checkedColor = Mas.colors.primary, checkmarkColor = Mas.colors.onPrimary)
                    )
                }
            }
        }
    }
}

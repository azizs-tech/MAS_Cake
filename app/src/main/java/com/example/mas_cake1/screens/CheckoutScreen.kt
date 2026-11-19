package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.Order
import com.example.mas_cake1.data.formatRub
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.InfoRow
import com.example.mas_cake1.ui.components.PriceBreakdown
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.components.masTextFieldColors
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun CheckoutScreen(
    onBackClicked: () -> Unit,
    onOrderPlaced: (Order) -> Unit
) {
    val settings = AppStore.settings
    var isDelivery by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(settings.userName) }
    var phone by remember { mutableStateOf(settings.userPhone) }
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var request by remember { mutableStateOf("") }

    val subtotal = AppStore.cartSubtotal()
    val discount = AppStore.cartDiscount()
    // Delivery fee is now really added to the total (before, the text said "from 300 ₽" but it was never charged).
    val delivery = AppStore.deliveryFeeFor(subtotal, isDelivery)
    val total = subtotal + delivery - discount

    val canConfirm = AppStore.cart.isNotEmpty() && name.isNotBlank() && phone.count { it.isDigit() } >= 10 &&
        date.isNotBlank() && (!isDelivery || address.isNotBlank())

    Scaffold(
        topBar = { BackTopBar(tr("Checkout", "Оформление"), onBackClicked) },
        containerColor = Mas.colors.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text(tr("How would you like to receive your order?", "Как вам удобнее получить заказ?"), fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Spacer(Modifier.height(8.dp))
            DeliveryOption(isDelivery, tr("Courier delivery (${formatRub(settings.deliveryFee)})", "Доставка курьером (${formatRub(settings.deliveryFee)})")) { isDelivery = true }
            DeliveryOption(!isDelivery, tr("Pickup from bakery (Free)", "Самовывоз из кондитерской (Бесплатно)")) { isDelivery = false }

            Spacer(Modifier.height(12.dp))
            InfoRow(Icons.Default.Schedule, tr("We work ${settings.openTime}–${settings.closeTime}", "Мы работаем ${settings.openTime}–${settings.closeTime}"))
            Spacer(Modifier.height(12.dp))

            CheckoutField(name, { name = it }, tr("Your name", "Ваше имя"))
            CheckoutField(phone, { phone = it }, tr("Phone number", "Номер телефона"), KeyboardType.Phone)
            if (isDelivery) CheckoutField(address, { address = it }, tr("Delivery address", "Адрес доставки"))
            CheckoutField(date, { date = it }, tr("Date and time", "Дата и время"), placeholder = tr("e.g., 15 May, 14:00", "Например: 15 мая, 14:00"))
            CheckoutField(
                request, { request = it }, tr("Special requests (optional)", "Пожелания (необязательно)"),
                placeholder = tr("Write 'Happy Birthday' on the cake", "Напишите «С днём рождения» на торте"), singleLine = false
            )

            Spacer(Modifier.height(8.dp))
            Text(tr("Payment", "Оплата"), fontWeight = FontWeight.Bold, color = Mas.colors.text)
            Text(tr("Card online / upon receipt", "Картой онлайн / при получении"), color = Mas.colors.textMuted)

            Spacer(Modifier.height(16.dp))
            PriceBreakdown(subtotal, delivery, discount, isDelivery)
            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                tr("Confirm Order — ${formatRub(total)}", "Подтвердить заказ — ${formatRub(total)}"),
                enabled = canConfirm,
                onClick = { onOrderPlaced(AppStore.placeOrder(name, phone, isDelivery, address, date, request)) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeliveryOption(selected: Boolean, text: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Mas.colors.primary))
        Text(text, color = Mas.colors.text)
    }
}

@Composable
private fun CheckoutField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    keyboard: KeyboardType = KeyboardType.Text,
    placeholder: String? = null,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        placeholder = if (placeholder != null) { { Text(placeholder, color = Mas.colors.textMuted) } } else null,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        shape = RoundedCornerShape(12.dp),
        colors = masTextFieldColors(),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}

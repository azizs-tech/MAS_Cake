package com.example.mas_cake1.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.components.LottieOrFallback
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun SuccessScreen(orderId: Int, onBackToHome: () -> Unit) {
    // Fake order tracking: the bar fills up over 5 seconds (feature 12)
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) { progress.animateTo(1f, tween(5000, easing = LinearEasing)) }

    val steps = listOf(
        tr("Accepted", "Принят"),
        tr("Preparing", "Готовится"),
        tr("On the way", "В пути"),
        tr("Delivered", "Доставлен")
    )
    val reached = (progress.value * (steps.size - 1) + 0.001f).toInt()

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieOrFallback(R.raw.success_check, Modifier.size(160.dp), loop = false) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Mas.colors.accent, modifier = Modifier.size(110.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text(tr("Thank you for your order!", "Спасибо за заказ!"), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Mas.colors.text, textAlign = TextAlign.Center)
        if (orderId > 0) Text(tr("Order #$orderId", "Заказ №$orderId"), fontSize = 18.sp, color = Mas.colors.accent, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            tr("We've started preparing your cake. We'll send you a notification with updates.", "Мы уже начали готовить ваш торт. Мы пришлём уведомление об изменении статуса."),
            textAlign = TextAlign.Center, color = Mas.colors.textMuted, fontSize = 15.sp
        )

        Spacer(Modifier.height(32.dp))

        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = Mas.colors.accent,
            trackColor = Mas.colors.divider,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth()) {
            steps.forEachIndexed { i, label ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(12.dp).background(if (i <= reached) Mas.colors.accent else Mas.colors.divider, CircleShape)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        label, fontSize = 12.sp, textAlign = TextAlign.Center,
                        color = if (i <= reached) Mas.colors.text else Mas.colors.textMuted,
                        fontWeight = if (i == reached) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(40.dp))
        PrimaryButton(tr("Back to Home", "На главную"), onClick = onBackToHome)
    }
}

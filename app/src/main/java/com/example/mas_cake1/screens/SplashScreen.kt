package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.R
import com.example.mas_cake1.ui.components.LanguageToggle
import com.example.mas_cake1.ui.components.LottieOrFallback
import com.example.mas_cake1.ui.components.PrimaryButton
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun SplashScreen(onStartClicked: () -> Unit, onLanguageToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LanguageToggle(onLanguageToggle)
        }

        Spacer(Modifier.weight(1f))

        // Animated cake (Lottie). Falls back to an emoji if the animation can't load.
        LottieOrFallback(R.raw.splash_cake, Modifier.size(200.dp)) {
            Text("🎂", fontSize = 96.sp)
        }

        Text(
            text = "MAS CAKE",
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Mas.colors.text,
            letterSpacing = 4.sp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = tr("Sweet moments start here", "Сладкие моменты начинаются здесь"),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Mas.colors.text,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = tr("Handmade cakes and desserts delivered across the city.", "Торты и десерты ручной работы с доставкой по городу."),
            fontSize = 16.sp,
            color = Mas.colors.accent,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        PrimaryButton(tr("Get started", "Начать"), onClick = onStartClicked, height = 60.dp)
    }
}

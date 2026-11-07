package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Мой профиль", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)

        Spacer(modifier = Modifier.height(24.dp))
        Text("Саид", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
        Text("+7 (999) 123-45-67", fontSize = 16.sp, color = ChocolateBrown.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(32.dp))

        ProfileMenuItem(Icons.Default.LocationOn, "Мои адреса")
        ProfileMenuItem(Icons.Default.CardGiftcard, "Программа лояльности")
        ProfileMenuItem(Icons.Default.Refresh, "Мои заказы")
        ProfileMenuItem(Icons.Default.FavoriteBorder, "Избранное")
        ProfileMenuItem(Icons.Default.DirectionsCar, "Условия доставки")
        ProfileMenuItem(Icons.Default.Call, "Контакты")
        ProfileMenuItem(Icons.Default.Settings, "Настройки")

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().clickable { /* Logout logic */ }.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = ChocolateBrown)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Выйти из профиля", color = ChocolateBrown, fontSize = 16.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = ChocolateBrown.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = ChocolateBrown, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go", tint = ChocolateBrown.copy(alpha = 0.5f))
    }
}
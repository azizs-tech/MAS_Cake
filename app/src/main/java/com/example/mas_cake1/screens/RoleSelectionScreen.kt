package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.components.BackTopBar
import com.example.mas_cake1.ui.components.LanguageToggle
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite
import com.example.mas_cake1.ui.theme.LightCaramel
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr

@Composable
fun RoleSelectionScreen(
    onCustomerSelected: () -> Unit,
    onAdminSelected: () -> Unit,
    onBackClicked: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    Scaffold(
        topBar = { BackTopBar("", onBackClicked) { LanguageToggle(onLanguageToggle) } },
        containerColor = Mas.colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = tr("Welcome to MAS Cake!", "Добро пожаловать в MAS Cake!"),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Mas.colors.text,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = tr("How do you want to use the app?", "Как вы хотите использовать приложение?"),
                fontSize = 17.sp,
                color = Mas.colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            RoleButton(Icons.Outlined.Cake, tr("I want to order cakes", "Я хочу заказать торты"), tr("Customer", "Клиент"), ChocolateBrown, onCustomerSelected)
            Spacer(Modifier.height(20.dp))
            RoleButton(Icons.Outlined.Storefront, tr("I run the bakery", "Я управляю кондитерской"), tr("Admin panel", "Панель администратора"), LightCaramel, onAdminSelected)
        }
    }
}

@Composable
private fun RoleButton(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(88.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = CreamWhite)
    ) {
        Icon(icon, null, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 14.sp, color = CreamWhite.copy(alpha = 0.8f))
        }
    }
}

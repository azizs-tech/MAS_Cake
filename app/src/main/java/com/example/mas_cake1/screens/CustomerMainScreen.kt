package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainScreen(
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Торты", "Акции", "Меню", "Корзина", "Профиль")
    val icons = listOf(
        Icons.Outlined.Cake, Icons.Outlined.LocalOffer, Icons.Outlined.RestaurantMenu, Icons.Outlined.ShoppingCart, Icons.Outlined.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CreamWhite, contentColor = ChocolateBrown) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title, fontSize = 12.sp) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ChocolateBrown,
                            selectedTextColor = ChocolateBrown,
                            indicatorColor = ChocolateBrown.copy(alpha = 0.1f),
                            unselectedIconColor = ChocolateBrown.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        containerColor = CreamWhite
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(onNavigateToProduct = onNavigateToProduct) // Torts (Cakes)
                1 -> CenterText("Акции (Promotions)") // Promos
                2 -> CenterText("Меню (Menu)") // Menu
                3 -> CartScreen(onCheckoutClicked = onNavigateToCheckout) // Cart
                4 -> ProfileScreen() // Profile
            }
        }
    }
}

@Composable
fun CenterText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = ChocolateBrown, fontSize = 20.sp)
    }
}
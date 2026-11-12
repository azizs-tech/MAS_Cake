package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.CartItem
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMainScreen(
    cartItems: List<CartItem>,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToPromo: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onSearchExecuted: (String) -> Unit, // <-- ADDED
    isEnglish: Boolean,
    onLanguageToggle: () -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onDeleteItem: (CartItem) -> Unit,
    onWeightChanged: (CartItem, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = if (isEnglish) listOf("Cakes", "Promos", "Menu", "Cart", "Profile")
    else listOf("Торты", "Акции", "Меню", "Корзина", "Профиль")

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
                        onClick = {
                            selectedTab = index
                            if (index == 1) onNavigateToPromo()
                        },
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
                0 -> HomeScreen(
                    onCategorySelected = onCategorySelected,
                    onNavigateToPromo = onNavigateToPromo,
                    onSearchExecuted = onSearchExecuted, // <-- PASSED
                    isEnglish = isEnglish,
                    onLanguageToggle = onLanguageToggle
                )
                1 -> CenterText(if (isEnglish) "Loading Promos..." else "Загрузка акций...")
                2 -> CenterText(if (isEnglish) "Menu" else "Меню")
                3 -> CartScreen(
                    cartItems = cartItems,
                    onCheckoutClicked = onNavigateToCheckout,
                    onIncreaseQuantity = onIncreaseQuantity,
                    onDecreaseQuantity = onDecreaseQuantity,
                    onDeleteItem = onDeleteItem,
                    onWeightChanged = onWeightChanged,
                    isEnglish = isEnglish
                )
                4 -> ProfileScreen()
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
package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.ui.theme.Mas
import com.example.mas_cake1.ui.theme.tr
import kotlinx.coroutines.launch

@Composable
fun CustomerMainScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onOpenCake: (String) -> Unit,
    onOpenCategory: (CakeCategory) -> Unit,
    onSearch: (query: String, filter: String) -> Unit,
    onCheckout: () -> Unit,
    onLanguageToggle: () -> Unit,
    onEditProfile: () -> Unit,
    onFavorites: () -> Unit,
    onMyOrders: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val addedText = tr("Added to cart ✔", "Добавлено в корзину ✔")
    val viewCartText = tr("View cart", "В корзину")

    // Used by the Promo tab: shows "Added to cart ✔" with a shortcut to the cart.
    val showAddedSnackbar: () -> Unit = {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(addedText, actionLabel = viewCartText, duration = SnackbarDuration.Short)
            if (result == SnackbarResult.ActionPerformed) onTabSelected(3)
        }
    }

    val tabs = listOf(
        tr("Cakes", "Торты") to Icons.Outlined.Cake,
        tr("Promos", "Акции") to Icons.Outlined.LocalOffer,
        tr("Menu", "Меню") to Icons.Outlined.RestaurantMenu,
        tr("Cart", "Корзина") to Icons.Outlined.ShoppingCart,
        tr("Profile", "Профиль") to Icons.Outlined.Person
    )
    val cartCount = AppStore.cartCount

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Mas.colors.surface, contentColor = Mas.colors.text) {
                tabs.forEachIndexed { index, (title, icon) ->
                    NavigationBarItem(
                        icon = {
                            if (index == 3 && cartCount > 0) {
                                BadgedBox(badge = { Badge { Text("$cartCount") } }) { Icon(icon, contentDescription = title) }
                            } else Icon(icon, contentDescription = title)
                        },
                        label = { Text(title, fontSize = 12.sp) },
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Mas.colors.text,
                            selectedTextColor = Mas.colors.text,
                            indicatorColor = Mas.colors.accent.copy(alpha = 0.25f),
                            unselectedIconColor = Mas.colors.textMuted,
                            unselectedTextColor = Mas.colors.textMuted
                        )
                    )
                }
            }
        },
        containerColor = Mas.colors.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onCategorySelected = onOpenCategory,
                    onOpenPromos = { onTabSelected(1) },
                    onOpenCake = onOpenCake,
                    onSearch = onSearch,
                    onLanguageToggle = onLanguageToggle
                )
                1 -> PromoContent(onOpenCake = onOpenCake, onAdded = showAddedSnackbar)
                2 -> MenuContent(onOpenCake = onOpenCake)
                3 -> CartScreen(onCheckoutClicked = onCheckout, onBrowse = { onTabSelected(2) })
                4 -> ProfileScreen(
                    onEditProfile = onEditProfile,
                    onFavorites = onFavorites,
                    onMyOrders = onMyOrders,
                    onSettings = onSettings,
                    onLogout = onLogout
                )
            }
        }
    }
}

package com.example.mas_cake1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mas_cake1.ui.screens.*
import com.example.mas_cake1.ui.theme.CreamWhite

data class CartItem(
    val name: String,
    var weight: String,
    var price: Int,
    val quantity: Int = 1
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = CreamWhite) {
                var currentScreen by remember { mutableStateOf("splash") }
                var previousScreen by remember { mutableStateOf("customerHome") }
                var selectedCake by remember { mutableStateOf("") }
                var selectedCategory by remember { mutableStateOf("") }
                var isEnglish by remember { mutableStateOf(true) }

                // NEW: State to hold what the user searched for
                var globalSearchQuery by remember { mutableStateOf("") }

                val cartItems = remember { mutableStateListOf<CartItem>() }

                fun addToCart(name: String, weight: String, price: Int) {
                    val existingItemIndex = cartItems.indexOfFirst { it.name == name && it.weight == weight }
                    if (existingItemIndex != -1) {
                        val existingItem = cartItems[existingItemIndex]
                        cartItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + 1)
                    } else {
                        cartItems.add(CartItem(name, weight, price, 1))
                    }
                }

                when (currentScreen) {
                    "splash" -> SplashScreen(onStartClicked = { currentScreen = "register" })
                    "register" -> RegisterScreen(
                        onSignUpClicked = { currentScreen = "roleSelection" },
                        onBackClicked = { currentScreen = "splash" }
                    )
                    "roleSelection" -> RoleSelectionScreen(
                        onCustomerSelected = { currentScreen = "customerHome" },
                        onAdminSelected = { },
                        onBackClicked = { currentScreen = "register" }
                    )

                    "customerHome" -> CustomerMainScreen(
                        cartItems = cartItems,
                        onNavigateToProduct = { cakeName ->
                            previousScreen = "customerHome"
                            selectedCake = cakeName
                            currentScreen = "productDetail"
                        },
                        onNavigateToCheckout = { currentScreen = "checkout" },
                        onNavigateToPromo = { currentScreen = "promo" },
                        onCategorySelected = { category ->
                            previousScreen = "customerHome"
                            selectedCategory = category
                            currentScreen = "categoryCakes"
                        },
                        onSearchExecuted = { query ->
                            globalSearchQuery = query
                            currentScreen = "searchResults"
                        },
                        isEnglish = isEnglish,
                        onLanguageToggle = { isEnglish = !isEnglish },
                        onIncreaseQuantity = { item ->
                            val index = cartItems.indexOf(item)
                            if (index != -1) cartItems[index] = item.copy(quantity = item.quantity + 1)
                        },
                        onDecreaseQuantity = { item ->
                            val index = cartItems.indexOf(item)
                            if (index != -1) {
                                if (item.quantity > 1) cartItems[index] = item.copy(quantity = item.quantity - 1)
                                else cartItems.remove(item)
                            }
                        },
                        onDeleteItem = { item -> cartItems.remove(item) },
                        onWeightChanged = { item, newWeight ->
                            val index = cartItems.indexOf(item)
                            if (index != -1) {
                                val newPrice = when (newWeight) {
                                    "1.5 кг" -> getCakePrice(item.name, "1.5")
                                    "2 кг" -> getCakePrice(item.name, "2")
                                    else -> getCakePrice(item.name, "1")
                                }
                                cartItems[index] = item.copy(weight = newWeight, price = newPrice)
                            }
                        }
                    )

                    "promo" -> PromoScreen(
                        onBackClicked = { currentScreen = "customerHome" },
                        onAddToCart = { name, weight, price ->
                            addToCart(name, weight, price)
                            currentScreen = "customerHome"
                        },
                        isEnglish = isEnglish
                    )

                    "categoryCakes" -> CategoryCakesScreen(
                        categoryName = selectedCategory,
                        onBackClicked = { currentScreen = "customerHome" },
                        onCakeSelected = { cakeName ->
                            previousScreen = "categoryCakes"
                            selectedCake = cakeName
                            currentScreen = "productDetail"
                        }
                    )

                    // NEW: Search Results Screen
                    "searchResults" -> SearchResultsScreen(
                        searchQuery = globalSearchQuery,
                        onBackClicked = { currentScreen = "customerHome" },
                        onCakeSelected = { cakeName ->
                            previousScreen = "searchResults"
                            selectedCake = cakeName
                            currentScreen = "productDetail"
                        }
                    )

                    "productDetail" -> ProductDetailScreen(
                        cakeName = selectedCake,
                        onBackClicked = { currentScreen = previousScreen },
                        onAddToCart = { name, weight, price ->
                            addToCart(name, weight, price)
                            currentScreen = "customerHome"
                        },
                        isEnglish = isEnglish
                    )

                    "checkout" -> CheckoutScreen(
                        totalPrice = cartItems.sumOf { it.price * it.quantity },
                        onBackClicked = { currentScreen = "customerHome" },
                        onOrderConfirmed = {
                            cartItems.clear()
                            currentScreen = "success"
                        },
                        isEnglish = isEnglish
                    )

                    "success" -> SuccessScreen(
                        onBackToHome = { currentScreen = "customerHome" }
                    )
                }
            }
        }
    }
}

fun getCakePrice(name: String, weightMultiplier: String): Int {
    val basePrice = when (name) {
        "Traditional Royal Fruitcake" -> 3500
        "Lemon & Elderflower Sponge" -> 2800
        "Yellow Sponge with Bavarian Cream" -> 3000
        "Chocolate Biscuit Groom's Cake" -> 1800
        "Classic White Vanilla Buttercream" -> 1600
        "Honey Cake (Medovik)" -> 3300
        "Napoleon" -> 2300
        "Chocolate Cake (Prague)" -> 2400
        "Cheesecake" -> 2100
        "Bento Cake" -> 2278
        "Птичье молоко (Ptichye Moloko)" -> 2600
        "Сметанник (Smetannik)" -> 535
        "Невский пирог (Nevsky Pirog)" -> 226
        "Мишка на севере (Mishka na Severe)" -> 2950
        "Прага (Prague)" -> 4200
        "Тирамису (Tiramisu)" -> 3345
        "Захерторт (Sachertorte)" -> 1120
        "Красный бархат (Red Velvet)" -> 924
        "Опера (Opéra)" -> 3500
        "Чёрный лес (Black Forest)" -> 2567
        else -> 3000
    }
    if (name == "Bento Cake") return basePrice
    return when (weightMultiplier) {
        "1.5" -> (basePrice * 1.5).toInt()
        "2" -> (basePrice * 2).toInt()
        else -> basePrice
    }
}
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = CreamWhite) {
                var currentScreen by remember { mutableStateOf("splash") }
                var selectedCake by remember { mutableStateOf("") }

                when (currentScreen) {
                    "splash" -> SplashScreen(onStartClicked = { currentScreen = "register" })
                    "register" -> RegisterScreen(
                        onSignUpClicked = { currentScreen = "roleSelection" },
                        onBackClicked = { currentScreen = "splash" }
                    )
                    "roleSelection" -> RoleSelectionScreen(
                        onCustomerSelected = { currentScreen = "customerHome" },
                        onAdminSelected = { /* Admin later */ },
                        onBackClicked = { currentScreen = "register" }
                    )
                    "customerHome" -> CustomerMainScreen(
                        onNavigateToProduct = { cakeName ->
                            selectedCake = cakeName
                            currentScreen = "productDetail"
                        },
                        onNavigateToCheckout = { currentScreen = "checkout" }
                    )
                    "productDetail" -> ProductDetailScreen(
                        cakeName = selectedCake,
                        onBackClicked = { currentScreen = "customerHome" },
                        onAddToCart = { currentScreen = "customerHome" }
                    )
                    "checkout" -> CheckoutScreen(
                        onBackClicked = { currentScreen = "customerHome" },
                        onOrderConfirmed = { currentScreen = "success" }
                    )
                    "success" -> SuccessScreen(
                        onBackToHome = { currentScreen = "customerHome" }
                    )
                }
            }
        }
    }
}
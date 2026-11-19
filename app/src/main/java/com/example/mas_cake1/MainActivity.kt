package com.example.mas_cake1

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.mas_cake1.data.AppStore
import com.example.mas_cake1.data.CakeCategory
import com.example.mas_cake1.notifications.OrderNotificationWorker
import com.example.mas_cake1.ui.screens.*
import com.example.mas_cake1.ui.screens.admin.*
import com.example.mas_cake1.ui.theme.LocalEnglish
import com.example.mas_cake1.ui.theme.MAS_CAKE1Theme
import com.example.mas_cake1.ui.theme.Mas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppStore.init(applicationContext)
        setContent { MasCakeApp() }
    }
}

/** One entry of the back stack. [arg] / [arg2] carry things like a cake id or a search query. */
data class Screen(val route: String, val arg: String = "", val arg2: String = "")

class Navigator(start: Screen) {
    val stack = mutableStateListOf(start)
    val current: Screen get() = stack.last()
    val canGoBack: Boolean get() = stack.size > 1

    fun go(route: String, arg: String = "", arg2: String = "") {
        stack.add(Screen(route, arg, arg2))
    }

    fun back() {
        if (stack.size > 1) stack.removeAt(stack.lastIndex)
    }

    /** Clears history, e.g. after logging out or finishing an order. */
    fun reset(route: String) {
        stack.clear()
        stack.add(Screen(route))
    }
}

@Composable
fun MasCakeApp() {
    val settings = AppStore.settings
    val context = LocalContext.current
    val nav = remember { Navigator(Screen("splash")) }

    // Tabs are remembered here, so coming back from a cake page keeps you on the same tab.
    var customerTab by rememberSaveable { mutableIntStateOf(0) }
    var adminTab by rememberSaveable { mutableIntStateOf(0) }

    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    var askedPermission by rememberSaveable { mutableStateOf(false) }

    fun openCustomer(tab: Int = 0) {
        customerTab = tab
        nav.reset("customer")
        if (!askedPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askedPermission = true
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val toggleLanguage = { AppStore.updateSettings { it.copy(isEnglish = !it.isEnglish) } }

    MAS_CAKE1Theme(darkTheme = settings.darkMode) {
        CompositionLocalProvider(LocalEnglish provides settings.isEnglish) {
            BackHandler(enabled = nav.canGoBack) { nav.back() }

            Surface(modifier = Modifier.fillMaxSize(), color = Mas.colors.background) {
                AnimatedContent(
                    targetState = nav.current,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screens"
                ) { screen ->
                    when (screen.route) {
                        // ---------------------------------------------------- onboarding
                        "splash" -> SplashScreen(
                            onStartClicked = {
                                if (settings.userName.isBlank()) nav.go("register") else nav.go("role")
                            },
                            onLanguageToggle = toggleLanguage
                        )

                        "register" -> RegisterScreen(
                            onSignUpClicked = { name, phone, email ->
                                AppStore.registerCustomer(name, phone, email)
                                nav.go("role")
                            },
                            onBackClicked = { nav.back() },
                            onLanguageToggle = toggleLanguage
                        )

                        "role" -> RoleSelectionScreen(
                            onCustomerSelected = { openCustomer() },
                            onAdminSelected = { adminTab = 0; nav.reset("admin") },
                            onBackClicked = { nav.back() },
                            onLanguageToggle = toggleLanguage
                        )

                        // ---------------------------------------------------- customer
                        "customer" -> CustomerMainScreen(
                            selectedTab = customerTab,
                            onTabSelected = { customerTab = it },
                            onOpenCake = { nav.go("product", it) },
                            onOpenCategory = { nav.go("category", it.name) },
                            onSearch = { query, filter -> nav.go("search", query, filter) },
                            onCheckout = { nav.go("checkout") },
                            onLanguageToggle = toggleLanguage,
                            onEditProfile = { nav.go("editProfile") },
                            onFavorites = { nav.go("favorites") },
                            onMyOrders = { nav.go("myOrders") },
                            onSettings = { nav.go("settings") },
                            onLogout = { nav.reset("splash") }
                        )

                        "product" -> ProductDetailScreen(
                            cakeId = screen.arg,
                            onBackClicked = { nav.back() },
                            onGoToCart = { openCustomer(tab = 3) }
                        )

                        "category" -> CategoryCakesScreen(
                            category = runCatching { CakeCategory.valueOf(screen.arg) }.getOrDefault(CakeCategory.USUAL),
                            onBackClicked = { nav.back() },
                            onCakeSelected = { nav.go("product", it) }
                        )

                        "search" -> SearchResultsScreen(
                            initialQuery = screen.arg,
                            initialFilter = screen.arg2,
                            onBackClicked = { nav.back() },
                            onCakeSelected = { nav.go("product", it) }
                        )

                        "checkout" -> CheckoutScreen(
                            onBackClicked = { nav.back() },
                            onOrderPlaced = { order ->
                                OrderNotificationWorker.schedule(context, order.id, settings.isEnglish)
                                nav.reset("customer")
                                nav.go("success", order.id.toString())
                            }
                        )

                        "success" -> SuccessScreen(
                            orderId = screen.arg.toIntOrNull() ?: 0,
                            onBackToHome = { openCustomer(tab = 0) }
                        )

                        "editProfile" -> EditProfileScreen(onBack = { nav.back() })
                        "favorites" -> FavoritesScreen(onBack = { nav.back() }, onOpenCake = { nav.go("product", it) })
                        "myOrders" -> MyOrdersScreen(onBack = { nav.back() })
                        "settings" -> SettingsScreen(onBack = { nav.back() })

                        // ---------------------------------------------------- admin
                        "admin" -> AdminMainScreen(
                            selectedTab = adminTab,
                            onTabSelected = { adminTab = it },
                            onOpenOrder = { nav.go("adminOrder", it.toString()) },
                            onEditCake = { nav.go("adminCakeEdit", it) },
                            onAddCake = { nav.go("adminCakeEdit", "") },
                            onOpenPromo = { nav.go("adminPromo") },
                            onOpenCustomers = { nav.go("adminCustomers") },
                            onExit = { nav.reset("role") }
                        )

                        "adminOrder" -> AdminOrderDetailScreen(
                            orderId = screen.arg.toIntOrNull() ?: 0,
                            onBack = { nav.back() }
                        )

                        "adminCakeEdit" -> AdminCakeEditScreen(
                            cakeId = screen.arg,
                            onBack = { nav.back() }
                        )

                        "adminPromo" -> AdminPromoScreen(onBack = { nav.back() })
                        "adminCustomers" -> AdminCustomersScreen(onBack = { nav.back() })
                    }
                }
            }
        }
    }
}

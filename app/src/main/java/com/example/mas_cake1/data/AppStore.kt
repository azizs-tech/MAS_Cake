package com.example.mas_cake1.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * App-wide state shared by the customer app and the admin panel.
 * Everything is saved locally with SharedPreferences, so it survives app restarts.
 * When you add a real backend later, only this file needs to change.
 */
object AppStore {

    private const val PREFS = "mas_cake_store"
    private lateinit var prefs: SharedPreferences
    private var initialized = false

    val cakes = mutableStateListOf<Cake>()
    val orders = mutableStateListOf<Order>()
    val cart = mutableStateListOf<CartItem>()
    val favorites = mutableStateListOf<String>()
    val recentlyViewed = mutableStateListOf<String>()
    val recentSearches = mutableStateListOf<String>()
    val registeredCustomers = mutableStateListOf<Customer>()

    var settings by mutableStateOf(AppSettings())
        private set

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        settings = loadSettings()
        cakes.addAll(loadCakes() ?: CakeCatalog.defaultCakes)
        orders.addAll(loadOrders() ?: seedOrders())
        favorites.addAll(loadStringList("favorites"))
        recentlyViewed.addAll(loadStringList("recentlyViewed"))
        recentSearches.addAll(loadStringList("recentSearches"))
        registeredCustomers.addAll(loadCustomers())
        saveCakes(); saveOrders()
    }

    // ---------------------------------------------------------------- settings
    fun updateSettings(change: (AppSettings) -> AppSettings) {
        settings = change(settings)
        saveSettings()
    }

    // ---------------------------------------------------------------- cakes
    fun cake(id: String): Cake? = cakes.firstOrNull { it.id == id }

    fun upsertCake(cake: Cake) {
        val index = cakes.indexOfFirst { it.id == cake.id }
        if (index == -1) cakes.add(cake) else cakes[index] = cake
        saveCakes()
    }

    fun deleteCake(id: String) {
        cakes.removeAll { it.id == id }
        cart.removeAll { it.cakeId == id }
        favorites.remove(id); recentlyViewed.remove(id)
        saveCakes(); saveStringList("favorites", favorites); saveStringList("recentlyViewed", recentlyViewed)
    }

    fun setAvailable(id: String, available: Boolean) {
        val c = cake(id) ?: return
        // Switching a cake back on with 0 stock gives it a fresh default stock.
        upsertCake(c.copy(available = available, stock = if (available && c.stock == 0) 10 else c.stock))
    }

    fun setOnPromo(id: String, onPromo: Boolean) {
        val c = cake(id) ?: return
        upsertCake(c.copy(onPromo = onPromo))
    }

    fun newCakeId(): String = "custom_" + System.currentTimeMillis()

    // ---------------------------------------------------------------- favorites / recent
    fun isFavorite(id: String) = id in favorites

    fun toggleFavorite(id: String) {
        if (id in favorites) favorites.remove(id) else favorites.add(0, id)
        saveStringList("favorites", favorites)
    }

    fun addRecentlyViewed(id: String) {
        recentlyViewed.remove(id)
        recentlyViewed.add(0, id)
        while (recentlyViewed.size > 3) recentlyViewed.removeAt(recentlyViewed.lastIndex)
        saveStringList("recentlyViewed", recentlyViewed)
    }

    fun addRecentSearch(query: String) {
        val q = query.trim()
        if (q.isEmpty()) return
        recentSearches.removeAll { it.equals(q, ignoreCase = true) }
        recentSearches.add(0, q)
        while (recentSearches.size > 3) recentSearches.removeAt(recentSearches.lastIndex)
        saveStringList("recentSearches", recentSearches)
    }

    fun clearRecentSearches() {
        recentSearches.clear()
        saveStringList("recentSearches", recentSearches)
    }

    // ---------------------------------------------------------------- cart
    fun addToCart(cakeId: String, weightKg: Double, quantity: Int = 1) {
        val index = cart.indexOfFirst { it.cakeId == cakeId && it.weightKg == weightKg }
        if (index == -1) cart.add(CartItem(cakeId, weightKg, quantity))
        else cart[index] = cart[index].copy(quantity = cart[index].quantity + quantity)
    }

    fun updateCartItem(index: Int, weightKg: Double, quantity: Int) {
        if (index !in cart.indices) return
        if (quantity <= 0) cart.removeAt(index) else cart[index] = cart[index].copy(weightKg = weightKg, quantity = quantity)
    }

    fun removeCartItem(index: Int) {
        if (index in cart.indices) cart.removeAt(index)
    }

    fun linePrice(item: CartItem): Int = (cake(item.cakeId)?.priceFor(item.weightKg) ?: 0) * item.quantity

    fun cartSubtotal(): Int = cart.sumOf { linePrice(it) }

    /** Discount only applies to cakes the admin put on promo. */
    fun cartDiscount(): Int {
        if (!settings.promoEnabled) return 0
        val promoSum = cart.filter { cake(it.cakeId)?.onPromo == true }.sumOf { linePrice(it) }
        return (promoSum * settings.discountPercent / 100.0).roundToInt()
    }

    fun deliveryFeeFor(subtotal: Int, isDelivery: Boolean): Int =
        if (!isDelivery || subtotal == 0 || subtotal >= settings.freeDeliveryFrom) 0 else settings.deliveryFee

    val cartCount get() = cart.sumOf { it.quantity }

    // ---------------------------------------------------------------- orders
    fun order(id: Int): Order? = orders.firstOrNull { it.id == id }

    fun placeOrder(
        name: String, phone: String, isDelivery: Boolean, address: String,
        dateTime: String, specialRequest: String
    ): Order {
        val subtotal = cartSubtotal()
        val discount = cartDiscount()
        val delivery = deliveryFeeFor(subtotal, isDelivery)
        val items = cart.mapNotNull { item ->
            val c = cake(item.cakeId) ?: return@mapNotNull null
            OrderItem(c.id, c.nameEn, item.weightKg, item.quantity, c.priceFor(item.weightKg))
        }
        val order = Order(
            id = settings.nextOrderId,
            customerName = name.trim(),
            phone = phone.trim(),
            isDelivery = isDelivery,
            address = if (isDelivery) address.trim() else "",
            dateTime = dateTime.trim(),
            specialRequest = specialRequest.trim(),
            items = items,
            subtotal = subtotal,
            deliveryFee = delivery,
            discount = discount,
            total = subtotal + delivery - discount,
            status = OrderStatus.NEW,
            createdAt = System.currentTimeMillis()
        )
        orders.add(0, order)
        // Take the sold cakes out of stock; a cake that runs out is switched off automatically.
        items.forEach { sold ->
            cake(sold.cakeId)?.let { c ->
                val left = (c.stock - sold.quantity).coerceAtLeast(0)
                upsertCake(c.copy(stock = left, available = c.available && left > 0))
            }
        }
        cart.clear()
        updateSettings { it.copy(nextOrderId = it.nextOrderId + 1) }
        saveOrders()
        return order
    }

    fun setOrderStatus(id: Int, status: OrderStatus) {
        val index = orders.indexOfFirst { it.id == id }
        if (index == -1) return
        orders[index] = orders[index].copy(status = status)
        saveOrders()
    }

    // ---------------------------------------------------------------- admin stats
    fun todayRevenue(): Int = orders.filter { isToday(it.createdAt) && it.status != OrderStatus.CANCELLED }.sumOf { it.total }

    fun countByStatus(status: OrderStatus) = orders.count { it.status == status }

    /** cakeId to number of pieces sold, best first. */
    fun bestSellers(): List<Pair<String, Int>> =
        orders.filter { it.status != OrderStatus.CANCELLED }
            .flatMap { it.items }
            .groupBy { it.cakeId }
            .map { (id, items) -> id to items.sumOf { it.quantity } }
            .sortedByDescending { it.second }

    /** Revenue for each of the last 7 days, oldest first. */
    fun revenueLast7Days(): List<Pair<Long, Int>> = (6 downTo 0).map { daysAgo ->
        val start = startOfDay(daysAgo)
        val end = start + DAY
        start to orders.filter { it.createdAt in start until end && it.status != OrderStatus.CANCELLED }.sumOf { it.total }
    }

    fun customers(): List<Customer> {
        val fromOrders = orders.groupBy { it.phone }.map { (phone, list) ->
            Customer(list.first().customerName, phone, list.size, list.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.total })
        }
        val known = fromOrders.map { it.phone }.toSet()
        return (fromOrders + registeredCustomers.filter { it.phone !in known }).sortedByDescending { it.ordersCount }
    }

    fun registerCustomer(name: String, phone: String, email: String) {
        updateSettings { it.copy(userName = name.trim(), userPhone = phone.trim(), userEmail = email.trim()) }
        if (registeredCustomers.none { it.phone == phone.trim() }) {
            registeredCustomers.add(Customer(name.trim(), phone.trim()))
            saveCustomers()
        }
    }

    // ---------------------------------------------------------------- dates
    const val DAY = 24L * 60 * 60 * 1000

    fun startOfDay(daysAgo: Int = 0): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -daysAgo)
    }.timeInMillis

    fun isToday(time: Long) = time >= startOfDay(0)

    // ---------------------------------------------------------------- sample data
    private fun seedOrders(): List<Order> {
        val now = System.currentTimeMillis()
        val h = 60L * 60 * 1000
        fun item(id: String, kg: Double, qty: Int): OrderItem {
            val c = CakeCatalog.defaultCakes.first { it.id == id }
            return OrderItem(id, c.nameEn, kg, qty, c.priceFor(kg))
        }
        fun order(
            id: Int, name: String, phone: String, items: List<OrderItem>, status: OrderStatus,
            created: Long, address: String = "ул. Тверская, 12, кв. 5", request: String = ""
        ): Order {
            val subtotal = items.sumOf { it.unitPrice * it.quantity }
            val delivery = if (address.isEmpty() || subtotal >= 7000) 0 else 300
            return Order(id, name, phone, address.isNotEmpty(), address, "", request, items, subtotal, delivery, 0, subtotal + delivery, status, created)
        }
        return listOf(
            order(1041, "Елена Соколова", "+7 (916) 555-12-34", listOf(item("medovik", 1.5, 1)), OrderStatus.OUT_FOR_DELIVERY, now - 3 * h),
            order(1040, "Дмитрий Козлов", "+7 (925) 444-22-11", listOf(item("napoleon", 1.0, 1), item("bento", 1.0, 2)), OrderStatus.NEW, now - 20 * 60 * 1000L, request = "Write 'Happy Birthday, Masha!' on the cake"),
            order(1039, "Мария Иванова", "+7 (903) 777-88-99", listOf(item("tiramisu", 1.0, 1)), OrderStatus.NEW, now - 35 * 60 * 1000L, address = ""),
            order(1038, "Иван Петров", "+7 (999) 123-45-67", listOf(item("medovik", 2.0, 1)), OrderStatus.NEW, now - 50 * 60 * 1000L),
            order(1037, "Анна Смирнова", "+7 (915) 222-33-44", listOf(item("cheesecake", 1.0, 1), item("medovik", 1.0, 1)), OrderStatus.PREPARING, now - 2 * h, request = "Less sugar, please"),
            order(1036, "Олег Новиков", "+7 (926) 101-20-30", listOf(item("prague_classic", 1.0, 1)), OrderStatus.PREPARING, now - 4 * h, address = ""),
            order(1035, "Мария Иванова", "+7 (903) 777-88-99", listOf(item("medovik", 1.0, 2)), OrderStatus.COMPLETED, now - 1 * DAY),
            order(1034, "Анна Смирнова", "+7 (915) 222-33-44", listOf(item("royal_fruitcake", 2.0, 1)), OrderStatus.COMPLETED, now - 2 * DAY),
            order(1033, "Иван Петров", "+7 (999) 123-45-67", listOf(item("napoleon", 1.5, 1)), OrderStatus.CANCELLED, now - 3 * DAY),
            order(1032, "Елена Соколова", "+7 (916) 555-12-34", listOf(item("medovik", 1.0, 1), item("ptichye_moloko", 1.0, 1)), OrderStatus.COMPLETED, now - 4 * DAY),
            order(1031, "Сергей Волков", "+7 (977) 909-80-70", listOf(item("tiramisu", 1.5, 1)), OrderStatus.COMPLETED, now - 5 * DAY, address = ""),
            order(1030, "Дмитрий Козлов", "+7 (925) 444-22-11", listOf(item("cheesecake", 2.0, 1)), OrderStatus.COMPLETED, now - 6 * DAY)
        )
    }

    // ---------------------------------------------------------------- persistence
    private fun saveSettings() {
        val s = settings
        prefs.edit().putString("settings", JSONObject().apply {
            put("isEnglish", s.isEnglish); put("darkMode", s.darkMode)
            put("userName", s.userName); put("userPhone", s.userPhone); put("userEmail", s.userEmail)
            put("promoEnabled", s.promoEnabled); put("discountPercent", s.discountPercent)
            put("deliveryFee", s.deliveryFee); put("freeDeliveryFrom", s.freeDeliveryFrom)
            put("openTime", s.openTime); put("closeTime", s.closeTime); put("contactPhone", s.contactPhone)
            put("nextOrderId", s.nextOrderId)
        }.toString()).apply()
    }

    private fun loadSettings(): AppSettings {
        val raw = prefs.getString("settings", null) ?: return AppSettings()
        return try {
            val o = JSONObject(raw)
            val d = AppSettings()
            AppSettings(
                isEnglish = o.optBoolean("isEnglish", d.isEnglish),
                darkMode = o.optBoolean("darkMode", d.darkMode),
                userName = o.optString("userName", d.userName),
                userPhone = o.optString("userPhone", d.userPhone),
                userEmail = o.optString("userEmail", d.userEmail),
                promoEnabled = o.optBoolean("promoEnabled", d.promoEnabled),
                discountPercent = o.optInt("discountPercent", d.discountPercent),
                deliveryFee = o.optInt("deliveryFee", d.deliveryFee),
                freeDeliveryFrom = o.optInt("freeDeliveryFrom", d.freeDeliveryFrom),
                openTime = o.optString("openTime", d.openTime),
                closeTime = o.optString("closeTime", d.closeTime),
                contactPhone = o.optString("contactPhone", d.contactPhone),
                nextOrderId = o.optInt("nextOrderId", d.nextOrderId)
            )
        } catch (e: Exception) {
            AppSettings()
        }
    }

    private fun saveCakes() {
        val arr = JSONArray()
        cakes.forEach { c ->
            arr.put(JSONObject().apply {
                put("id", c.id); put("nameEn", c.nameEn); put("nameRu", c.nameRu)
                put("category", c.category.name); put("price", c.pricePerKg)
                put("imageKey", c.imageKey); put("imagePath", c.imagePath)
                put("descEn", c.descEn); put("descRu", c.descRu)
                put("tags", JSONArray(c.tags.map { it.name }))
                put("perPiece", c.isPerPiece); put("available", c.available)
                put("stock", c.stock); put("onPromo", c.onPromo)
            })
        }
        prefs.edit().putString("cakes", arr.toString()).apply()
    }

    private fun loadCakes(): List<Cake>? {
        val raw = prefs.getString("cakes", null) ?: return null
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val tags = o.optJSONArray("tags")
                Cake(
                    id = o.getString("id"),
                    nameEn = o.optString("nameEn"),
                    nameRu = o.optString("nameRu"),
                    category = CakeCategory.valueOf(o.optString("category", "USUAL")),
                    pricePerKg = o.optInt("price"),
                    imageKey = o.optString("imageKey"),
                    imagePath = o.optString("imagePath"),
                    descEn = o.optString("descEn"),
                    descRu = o.optString("descRu"),
                    tags = if (tags == null) emptySet() else (0 until tags.length()).mapNotNull { j ->
                        runCatching { CakeTag.valueOf(tags.getString(j)) }.getOrNull()
                    }.toSet(),
                    isPerPiece = o.optBoolean("perPiece"),
                    available = o.optBoolean("available", true),
                    stock = o.optInt("stock", 10),
                    onPromo = o.optBoolean("onPromo")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun saveOrders() {
        val arr = JSONArray()
        orders.forEach { o ->
            arr.put(JSONObject().apply {
                put("id", o.id); put("name", o.customerName); put("phone", o.phone)
                put("isDelivery", o.isDelivery); put("address", o.address); put("dateTime", o.dateTime)
                put("request", o.specialRequest); put("subtotal", o.subtotal); put("deliveryFee", o.deliveryFee)
                put("discount", o.discount); put("total", o.total); put("status", o.status.name); put("createdAt", o.createdAt)
                put("items", JSONArray().apply {
                    o.items.forEach { i ->
                        put(JSONObject().apply {
                            put("cakeId", i.cakeId); put("name", i.name); put("kg", i.weightKg)
                            put("qty", i.quantity); put("unit", i.unitPrice)
                        })
                    }
                })
            })
        }
        prefs.edit().putString("orders", arr.toString()).apply()
    }

    private fun loadOrders(): List<Order>? {
        val raw = prefs.getString("orders", null) ?: return null
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { idx ->
                val o = arr.getJSONObject(idx)
                val items = o.getJSONArray("items")
                Order(
                    id = o.getInt("id"),
                    customerName = o.optString("name"),
                    phone = o.optString("phone"),
                    isDelivery = o.optBoolean("isDelivery"),
                    address = o.optString("address"),
                    dateTime = o.optString("dateTime"),
                    specialRequest = o.optString("request"),
                    items = (0 until items.length()).map { j ->
                        val i = items.getJSONObject(j)
                        OrderItem(i.optString("cakeId"), i.optString("name"), i.optDouble("kg", 1.0), i.optInt("qty", 1), i.optInt("unit"))
                    },
                    subtotal = o.optInt("subtotal"),
                    deliveryFee = o.optInt("deliveryFee"),
                    discount = o.optInt("discount"),
                    total = o.optInt("total"),
                    status = runCatching { OrderStatus.valueOf(o.optString("status")) }.getOrDefault(OrderStatus.NEW),
                    createdAt = o.optLong("createdAt")
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun saveCustomers() {
        val arr = JSONArray()
        registeredCustomers.forEach { arr.put(JSONObject().apply { put("name", it.name); put("phone", it.phone) }) }
        prefs.edit().putString("customers", arr.toString()).apply()
    }

    private fun loadCustomers(): List<Customer> {
        val raw = prefs.getString("customers", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { Customer(arr.getJSONObject(it).optString("name"), arr.getJSONObject(it).optString("phone")) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveStringList(key: String, list: List<String>) {
        prefs.edit().putString(key, JSONArray(list).toString()).apply()
    }

    private fun loadStringList(key: String): List<String> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { arr.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

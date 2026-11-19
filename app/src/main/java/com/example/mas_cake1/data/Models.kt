package com.example.mas_cake1.data

import kotlin.math.roundToInt

enum class CakeCategory(val titleEn: String, val titleRu: String) {
    WEDDING("Wedding", "Свадебные"),
    BIRTHDAY("Birthday", "Детские"),
    USUAL("Usual", "Обычные");

    fun title(english: Boolean) = if (english) titleEn else titleRu
    fun longTitle(english: Boolean) = if (english) "$titleEn Cakes" else "$titleRu торты"
}

enum class CakeTag(val titleEn: String, val titleRu: String) {
    CHOCOLATE("Chocolate", "Шоколадные"),
    FRUIT("Fruit", "Фруктовые"),
    BESTSELLER("Bestsellers", "Хиты продаж");

    fun title(english: Boolean) = if (english) titleEn else titleRu
}

data class Cake(
    val id: String,
    val nameEn: String,
    val nameRu: String,
    val category: CakeCategory,
    /** Price for 1 kg, or for one piece when [isPerPiece] is true. */
    val pricePerKg: Int,
    /** Key of a built-in drawable (see CakeCatalog.drawableFor). Empty for admin-added cakes. */
    val imageKey: String = "",
    /** Absolute path of an image picked by the admin and copied into app storage. */
    val imagePath: String = "",
    val descEn: String = "",
    val descRu: String = "",
    val tags: Set<CakeTag> = emptySet(),
    val isPerPiece: Boolean = false,
    val available: Boolean = true,
    val stock: Int = 10,
    val onPromo: Boolean = false
) {
    fun name(english: Boolean) = if (english) nameEn else nameRu
    fun description(english: Boolean) = if (english) descEn else descRu
    fun priceFor(weightKg: Double): Int =
        if (isPerPiece) pricePerKg else (pricePerKg * weightKg).roundToInt()

    val isLowStock get() = available && stock in 1..3
    val canOrder get() = available && stock > 0
}

data class CartItem(
    val cakeId: String,
    val weightKg: Double,
    val quantity: Int = 1
)

enum class OrderStatus(val titleEn: String, val titleRu: String) {
    NEW("New", "Новый"),
    PREPARING("Preparing", "Готовится"),
    OUT_FOR_DELIVERY("Out for delivery", "В пути"),
    COMPLETED("Completed", "Выполнен"),
    CANCELLED("Cancelled", "Отменён");

    fun title(english: Boolean) = if (english) titleEn else titleRu

    val next: OrderStatus?
        get() = when (this) {
            NEW -> PREPARING
            PREPARING -> OUT_FOR_DELIVERY
            OUT_FOR_DELIVERY -> COMPLETED
            else -> null
        }

    val isActive get() = this == NEW || this == PREPARING || this == OUT_FOR_DELIVERY
}

data class OrderItem(
    val cakeId: String,
    val name: String,
    val weightKg: Double,
    val quantity: Int,
    val unitPrice: Int
)

data class Order(
    val id: Int,
    val customerName: String,
    val phone: String,
    val isDelivery: Boolean,
    val address: String,
    val dateTime: String,
    val specialRequest: String,
    val items: List<OrderItem>,
    val subtotal: Int,
    val deliveryFee: Int,
    val discount: Int,
    val total: Int,
    val status: OrderStatus,
    val createdAt: Long
)

data class Customer(
    val name: String,
    val phone: String,
    val ordersCount: Int = 0,
    val totalSpent: Int = 0
)

data class AppSettings(
    val isEnglish: Boolean = true,
    val darkMode: Boolean = false,
    // Customer profile (saved locally)
    val userName: String = "",
    val userPhone: String = "",
    val userEmail: String = "",
    // Business settings (edited by the admin)
    val promoEnabled: Boolean = true,
    val discountPercent: Int = 15,
    val deliveryFee: Int = 300,
    val freeDeliveryFrom: Int = 7000,
    val openTime: String = "09:00",
    val closeTime: String = "21:00",
    val contactPhone: String = "+7 (495) 123-45-67",
    val nextOrderId: Int = 1042
)

/** "1 kg", "1.5 кг" ... */
fun formatKg(kg: Double, english: Boolean): String {
    val number = if (kg % 1.0 == 0.0) kg.toInt().toString() else kg.toString()
    return number + if (english) " kg" else " кг"
}

/** 12500 -> "12 500 ₽" */
fun formatRub(amount: Int): String {
    val digits = kotlin.math.abs(amount).toString().reversed().chunked(3).joinToString(" ").reversed()
    return (if (amount < 0) "-" else "") + digits + " ₽"
}

val WEIGHT_OPTIONS = listOf(1.0, 1.5, 2.0)

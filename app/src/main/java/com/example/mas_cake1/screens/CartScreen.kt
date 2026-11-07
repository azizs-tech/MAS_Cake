package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.CartItem
import com.example.mas_cake1.ui.theme.ChocolateBrown

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onCheckoutClicked: () -> Unit,
    onIncreaseQuantity: (CartItem) -> Unit,
    onDecreaseQuantity: (CartItem) -> Unit,
    onDeleteItem: (CartItem) -> Unit,
    onWeightChanged: (CartItem, String) -> Unit, // <-- ADDED
    isEnglish: Boolean
) {
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isEnglish) "Your Order" else "Ваш заказ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    if (isEnglish) "Cart is empty" else "Корзина пуста",
                    color = ChocolateBrown.copy(alpha = 0.5f),
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cartItems.size) { index ->
                    val item = cartItems[index]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // TOP ROW: Name, Delete, Price
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.name,
                                    color = ChocolateBrown,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "${item.price * item.quantity} ₽",
                                    fontWeight = FontWeight.Bold,
                                    color = ChocolateBrown,
                                    fontSize = 16.sp
                                )
                                IconButton(onClick = { onDeleteItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }

                            // MIDDLE ROW: Weight Selector (1 кг, 1.5 кг, 2 кг)
                            // Bento cake doesn't use weight, so we hide it for Bento
                            if (item.name != "Bento Cake") {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("1 кг", "1.5 кг", "2 кг").forEach { weightOption ->
                                        FilterChip(
                                            selected = item.weight == weightOption,
                                            onClick = { onWeightChanged(item, weightOption) },
                                            label = { Text(weightOption, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ChocolateBrown,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // BOTTOM ROW: Quantity Controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDecreaseQuantity(item) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ChocolateBrown)
                                }

                                Text(
                                    text = "${item.quantity}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChocolateBrown,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                IconButton(
                                    onClick = { onIncreaseQuantity(item) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = ChocolateBrown)
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (isEnglish) "Total:" else "Итого:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
            Text("$totalPrice ₽", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ChocolateBrown)
        }

        Button(
            onClick = onCheckoutClicked,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown),
            enabled = cartItems.isNotEmpty()
        ) {
            Text(if (isEnglish) "Proceed to Checkout" else "Перейти к оформлению", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
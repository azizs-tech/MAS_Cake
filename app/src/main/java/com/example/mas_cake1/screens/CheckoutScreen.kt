package com.example.mas_cake1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mas_cake1.ui.theme.ChocolateBrown
import com.example.mas_cake1.ui.theme.CreamWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClicked: () -> Unit,
    onOrderConfirmed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = ChocolateBrown) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ChocolateBrown)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CreamWhite)
            )
        },
        containerColor = CreamWhite
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Confirm your order",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onOrderConfirmed,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChocolateBrown)
            ) {
                Text("Confirm Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
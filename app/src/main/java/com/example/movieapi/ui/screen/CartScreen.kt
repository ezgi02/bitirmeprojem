package com.example.movieapi.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.movieapi.data.CartItem
import com.example.movieapi.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel = viewModel()) {

   // Log.d("CartScreen", "Cart Items in UI: $cartItems")


    val userName = "ezgi" // Dinamik yapabilirsiniz

    LaunchedEffect(Unit) {
        cartViewModel.fetchCart(userName)
    }


    val cartItems = cartViewModel.cart.collectAsState().value

    Log.d("CartScreen", "Cart Items in UI: $cartItems")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sepetim") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (cartItems.isNullOrEmpty()) {
                    item {
                        Text(
                            text = "Sepetiniz boş.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(cartItems) { cartItem ->
                        CartItemRow(cartItem = cartItem) {
                            cartViewModel.removeFromCart(cartItem.cartId, "ezgi")
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Toplam fiyat alanı
            val totalPrice = cartItems.sumOf { it.price * it.orderAmount }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Toplam:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${totalPrice}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = {
                    // Ödeme ekranına yönlendirme yapılabilir
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Sepeti Onayla")
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = cartItem.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Adet: ${cartItem.orderAmount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Fiyat: $${cartItem.price}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Sil")
        }
    }
}

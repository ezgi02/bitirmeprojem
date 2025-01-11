package com.example.movieapi.ui.screen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.movieapi.data.entity.CartItem
import com.example.movieapi.ui.theme.TitleColor
import com.example.movieapi.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: MovieViewModel = viewModel()) {

   // Log.d("CartScreen", "Cart Items in UI: $cartItems")

    val context = LocalContext.current // Toast için context
 //  val userName = "ezgi" // Dinamik yapabilirsiniz
    val userName=viewModel.userName.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.fetchCart(userName)
        Log.e("Username",userName)
    }


    val cartItems = viewModel.cart.collectAsState().value
    // Gruplandırılmış sepet (aynı ürünleri birleştirip miktar ekler)
    val groupedCartItems = cartItems.groupBy { it.name }.map { (name, items) ->
        items.first().copy(orderAmount = items.size)
    }

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
                if (groupedCartItems.isNullOrEmpty()) {
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
                    items(groupedCartItems) { cartItem ->
                        CartItemRow(cartItem = cartItem) {
                            viewModel.removeFromCart(cartItem.cartId, userName)
                            // Toast ile ürün silindi mesajı
                            Toast.makeText(context, "${cartItem.name} sepetten silindi", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            // Toplam fiyat alanı
            val totalPrice = groupedCartItems.sumOf { it.price * it.orderAmount }
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
                    .padding(16.dp),colors = ButtonDefaults.buttonColors(
                    containerColor = TitleColor, // Arka plan rengi (Mor)
                    contentColor = Color.White // Metin rengi (Beyaz)
                )
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

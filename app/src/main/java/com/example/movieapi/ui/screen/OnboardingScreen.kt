package com.example.movieapi.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapi.R
import com.example.movieapi.ui.theme.TitleColor
import com.example.movieapi.viewmodel.MovieViewModel

@Composable
fun OnboardingScreen(viewModel: MovieViewModel = viewModel(), onFinish: (String) -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    var userName by remember { mutableStateOf("") }

    val pages = listOf(
        OnboardingPage(
            title = "Favori Filmlerinizi Satın Almaya Hazır Mısınız?",
            description = "En sevdiğiniz filmleri keşfedin, detaylarına göz atın ve kolayca satın alın.",
            image = R.drawable.onboarding1
        ),
        OnboardingPage(
            title = "Favorilerinizi Oluşturun",
            description = "Beğendiğiniz filmleri favorilerinize ekleyerek daha sonra kolayca satın alma işlemi yapabilirsiniz.",
            image = R.drawable.onboarding2
        ),
        OnboardingPage(
            title = "Kişiselleştirin!",
            description = "Lütfen devam etmeden önce adınızı girin.",
            image = R.drawable.onboarding3,
            isInputPage = true
        )
    )

    val page = pages[currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sayfa İçeriği
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        if (page.isInputPage) {
            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Adınız") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Butonlar:Geri, İleri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Dikey ortalama
        ) {
            if (currentPage > 0) {
                TextButton(onClick = { currentPage-- }) {
                    Text(text = "Geri")
                }
            }

            Button(onClick = {
                if (page.isInputPage && userName.isBlank()) {
                    // Kullanıcı ismi girmediyse, hata göster
                    return@Button
                }

                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    viewModel.updateUserName(userName)
                    onFinish(userName)
                }
            },  modifier = Modifier
                .width(150.dp) // Buton genişliğini ekranın tamamına yay
                .padding(vertical = 16.dp) // Dikey boşluk ekle
                .height(40.dp), // Butonun yüksekliğini artır
                colors = ButtonDefaults.buttonColors(
                    containerColor = TitleColor, // Buton arka plan rengi (Mor)
                    contentColor = Color.White // Buton yazı rengi (Beyaz)
                )
            ) {
                Text(text = if (currentPage == pages.size - 1) "Başla" else "İleri")
            }
        }
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int,
    val isInputPage: Boolean = false
)
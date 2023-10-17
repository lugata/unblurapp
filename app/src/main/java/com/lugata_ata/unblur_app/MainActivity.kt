package com.lugata_ata.unblur_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lugata_ata.unblur_app.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Menggunakan ViewModel untuk berbagi data antara UploadActivity dan ResultActivity
                    val viewModel = remember { MainViewModel() }
                    Nav(viewModel)
                }
            }
        }

    }
}

class MainViewModel : ViewModel() {
    // Menyimpan gambar yang diunggah oleh pengguna dan hasil dari API After
    val uploadedImage = mutableStateOf<String?>(null)
    val afterImage = mutableStateOf<String?>(null)

}


@Composable
fun Nav(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Splash" ){
        composable(route = "Splash") {
            SplashScreen(navController)
        }
        composable(route = "Upload") {
            UploadActivity(navController, viewModel)
        }
        composable(route = "Result"){
            ResultActivity(navController, viewModel)

        }
    }
}
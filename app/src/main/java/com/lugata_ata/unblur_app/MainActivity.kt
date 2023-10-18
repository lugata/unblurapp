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

/**
 * Kelas MainActivity adalah kelas utama yang menampilkan aplikasi UnBlur.
 * Kelas ini mengatur tampilan aplikasi dan menggunakan ViewModel untuk berbagi data antara UploadActivity dan ResultActivity.
 */
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

/**
 * Kelas MainViewModel adalah kelas ViewModel yang digunakan untuk berbagi data antara UploadActivity dan ResultActivity.
 * Kelas ini menyimpan gambar yang diunggah oleh pengguna dan hasil dari API After.
 */
class MainViewModel : ViewModel() {
    val uploadedImage = mutableStateOf<String?>(null)
    val afterImage = mutableStateOf<String?>(null)
}

/**
 * Fungsi Nav adalah fungsi yang menampilkan navigasi antara tiga activity: SplashScreen, UploadActivity, dan ResultActivity.
 * Fungsi ini menggunakan NavHost dan NavController untuk mengatur navigasi.
 * Fungsi ini juga menggunakan MainViewModel untuk berbagi data antara UploadActivity dan ResultActivity.
 */
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
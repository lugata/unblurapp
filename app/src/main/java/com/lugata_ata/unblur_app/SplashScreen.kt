package com.lugata_ata.unblur_app

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    // Izin yang diperlukan
    val permissions = listOf(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_MEDIA_LOCATION,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // Periksa apakah semua izin sudah diberikan
    val allPermissionsGranted = remember(permissions) {
        permissions.all {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Mainkan animasi splash screen

    // Tampilan SplashScreen dengan latar belakang animasi
    // Tampilan SplashScreen dengan latar belakang animasi
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // AestheticBackground adalah komponen untuk latar belakang animasi
        AestheticBackground()
        // Ganti teks dengan gambar logo SVG menggunakan ImageVector
        val logoPainter = rememberVectorPainter(image = ImageVector.vectorResource(R.drawable.logounblurapp)) // Ganti dengan lokasi gambar SVG Anda


        // Gunakan Column untuk menampilkan teks di bawah logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = logoPainter,
                contentDescription = null,
                modifier = Modifier.size(140.dp) // Sesuaikan ukuran logo
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Unblur App",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        if (permissionsResult.all { it.value }) {
            // Semua izin telah diberikan, navigasikan ke "UploadActivity"


                navController.navigate("Upload")

        } else {
            // Tampilkan pesan bahwa izin diperlukan untuk menggunakan aplikasi
                Toast.makeText(
                    context,
                    "Izin diperlukan untuk menggunakan aplikasi",
                    Toast.LENGTH_SHORT
                ).show()

            activity?.finish()
        }
    }

    // Meminta izin jika belum diberikan
    if (!allPermissionsGranted) {
        // Implementasikan logika meminta izin di sini
        LaunchedEffect(key1 = true) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
            delay(2400)
        }
    } else {
        // Jika semua izin sudah diberikan, navigasikan ke UploadActivity
        LaunchedEffect(key1 = true) {
            delay(2400)
            navController.navigate("Upload")
        }
    }


}

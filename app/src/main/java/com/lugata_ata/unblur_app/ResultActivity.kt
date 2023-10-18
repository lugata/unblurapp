package com.lugata_ata.unblur_app

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.smarttoolfactory.beforeafter.BeforeAfterLayout
import com.smarttoolfactory.beforeafter.OverlayStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/**
 * Fungsi ini menampilkan hasil dari proses pengaburan gambar. Fungsi ini menerima dua parameter,
 * yaitu [navController] dan [viewModel]. [navController] digunakan untuk navigasi antar halaman,
 * sedangkan [viewModel] digunakan untuk mengakses data yang dibutuhkan.
 *
 * Fungsi ini mengambil dua gambar, yaitu gambar sebelum dan sesudah proses pengaburan. Gambar-gambar
 * tersebut ditampilkan dalam satu layout yang memungkinkan pengguna untuk membandingkan keduanya.
 * Selain itu, fungsi ini juga menyediakan tombol untuk kembali ke halaman sebelumnya dan tombol
 * untuk mengunduh gambar hasil pengaburan.
 *
 * @param navController NavController yang digunakan untuk navigasi antar halaman.
 * @param viewModel MainViewModel yang digunakan untuk mengakses data yang dibutuhkan.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultActivity(navController: NavController, viewModel: MainViewModel) {

    val uploadedImage = viewModel.uploadedImage.value
    val afterImage = viewModel.afterImage.value

    val uploadedImagePainter = rememberAsyncImagePainter(model = uploadedImage)
    val afterImagePainter = rememberAsyncImagePainter(model = afterImage)

    // A flag to keep track of whether both images have loaded
    var imagesLoaded by remember { mutableStateOf(false) }


    // Load images in a LaunchedEffect
    LaunchedEffect(uploadedImage, afterImage) {
        if (uploadedImage != null && afterImage != null) {
            imagesLoaded = true
        }
    }
    val context = LocalContext.current // Get the context using LocalContext

    // Function to download the image asynchronously
    val downloadImageAsync: suspend () -> Unit = {
        afterImage?.let { imageUrl ->
            downloadImage(context, imageUrl)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Aesthetic background gradient
        AestheticBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
        ) {
            // Create a top bar for buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add a button to return to the MainActivity
                IconButton(
                    onClick = {
                        navController.popBackStack() // Use popBackStack to go back
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        modifier = Modifier
                            .height(34.dp)
                            .padding(0.dp),
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }

                // Add a download button (customize this based on your download logic)
                OutlinedButton(
                    modifier = Modifier
                        .size(100.dp, 34.dp)
                        .align(Alignment.CenterVertically)
                        .padding(0.dp, 0.dp, 8.dp, 0.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color.White),
                    contentPadding = PaddingValues(2.dp),  // Avoid the little icon
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),

                    onClick = {
                        // Add download logic here
                        CoroutineScope(Dispatchers.IO).launch {
                            downloadImageAsync()
                        }
                    },
                ) {
                    Text("Download")
                }
            }
            

            if (imagesLoaded) {
                BeforeAfterLayout(
                    modifier = Modifier.fillMaxSize(),
                    beforeContent = {
                        // Define the content for the "Before" side
                        // This can be an image, a video, or any other Composable
                        Image(
                            painter = uploadedImagePainter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    afterContent = {
                        // Define the content for the "After" side
                        // This can be an image, a video, or any other Composable
                        Image(
                            painter = afterImagePainter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    },
                    enableZoom = true, // Enable zooming
                    enableProgressWithTouch = true, // Allow changing progress with touch
                    overlayStyle = OverlayStyle(
                        // Customize overlay style if needed
                        dividerColor = Color(0xffF44336),
                        dividerWidth = 2.dp,
                        thumbShape = CutCornerShape(8.dp),
                        thumbBackgroundColor = Color.Red,
                        thumbTintColor = Color.White
                    ),
                )
            } else {
                // Display a loading indicator or placeholder until both images are loaded
                CircularProgressIndicator()
            }
        }
    }
}

suspend fun downloadImage(context: Context, imageUrl: String) {
    withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Unblur_$timestamp.png"

        try {
            val connection = URL(imageUrl).openConnection()
            connection.connect()

            val inputStream = connection.getInputStream()

            // Change the download directory here
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "unblurApp")

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var len: Int

            while (inputStream.read(buffer).also { len = it } > 0) {
                outputStream.write(buffer, 0, len)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Notify the MediaScanner about the new file so that it appears in the gallery apps
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                null,
                null
            )

            // Menampilkan Toast di utas utama
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Download completed.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exceptions and log them
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: $e", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    ResultActivity(navController = NavController(LocalContext.current), viewModel = MainViewModel())
}

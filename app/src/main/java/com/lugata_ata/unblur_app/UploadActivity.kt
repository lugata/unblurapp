package com.lugata_ata.unblur_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun UploadActivity(navController: NavController, viewModel: MainViewModel) {

    // Variabel untuk menyimpan gambar yang dipilih
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    // Variabel untuk menandakan apakah sedang proses upload atau tidak
    var isUploading by remember { mutableStateOf(false) }

    // Context dari activity saat ini
    val context = LocalContext.current

    // Coroutine scope untuk menjalankan proses upload
    val scope = rememberCoroutineScope()

    // Retrofit service untuk mengirim gambar ke After API
    val afterService = remember {
        Retrofit.Builder()
            .baseUrl("https://api.replicate.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AfterService::class.java)
    }

    //UI
    // Stroke untuk border pada kotak upload
    val stroke = Stroke(width = 8f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    // Fungsi untuk memilih gambar dari galeri
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri ->
            val inputStream = context.contentResolver.openInputStream(imageUri)
            selectedImage = BitmapFactory.decodeStream(inputStream)

            // Set isUploading menjadi true dan jalankan proses upload
            if (selectedImage != null) {
                isUploading = true
                scope.launch {
                    try {
                        val afterResponse = sendImageToAfter(selectedImage!!, afterService)
                        isUploading = false
                        if (afterResponse.status == "succeeded") {
                            // Ambil gambar hasil dari After API
                            val base64AfterImage = afterResponse.output

                            // Simpan URL dari gambar yang diupload
                            viewModel.uploadedImage.value = afterResponse.input.image
                            viewModel.afterImage.value = base64AfterImage

                            // Navigate ke halaman Result
                            navController.navigate("Result")
                        } else {
                            Log.e("UploadActivity", "Error: ${afterResponse.status}")
                            Toast.makeText(context, "Error: ${afterResponse.status}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: HttpException) {
                        // Handle HTTP 422 error
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.e("UploadActivity", "HTTP 422 Error: $errorBody")
                        // Handle error dan tampilkan pesan yang sesuai ke user
                        Toast.makeText(context, "HTTP 422 Error: $errorBody", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        // Handle error lainnya
                        Log.e("UploadActivity", "Error uploading image to After", e)
                        // Tampilkan pesan error ke user
                        Toast.makeText(context, "Error uploading image to After", Toast.LENGTH_SHORT).show()
                    } finally {
                        isUploading = false // Set isUploading menjadi false di luar try-catch block
                    }
                }
            }
        }
    }

    // Tampilan UI
    Box(
        modifier = Modifier
            .fillMaxSize(),

        contentAlignment = Alignment.Center
    ) {
        AestheticBackground()
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                text = "AI UnBlur photo in one click",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 54.sp // Set line height di sini

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your one-stop solution for quick and easy image unblurring.",
                color = Color.LightGray,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp // Set line height di sini
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.98f)
                    .fillMaxHeight(0.6f)

                    .drawBehind {
                        drawRoundRect(
                            color = Color.LightGray,
                            style = stroke,
                            cornerRadius = CornerRadius(24.dp.toPx())
                        )
                    }
                    .clickable { getContent.launch("image/*") }
            ) {
                Text(
                    text = "Click to Upload",
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = Color.Gray,
                    fontSize = 18.sp,

                )
            }
        }

        // Tampilkan progress bar jika sedang proses upload
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .clip(RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}

// Fungsi untuk membuat background dengan efek parallax
@Composable
fun AestheticBackground() {
    var xOffset by remember { mutableFloatStateOf(0f) }
    var yOffset by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val xOffsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val yOffsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    LaunchedEffect(key1 = xOffsetAnimation, key2 = yOffsetAnimation) {
        while (true) {
            xOffset = xOffsetAnimation
            yOffset = yOffsetAnimation
            delay(10000) // Adjust this delay as needed
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Create a gradient brush
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFF710275), Color(0xFF001D50)),
            start = Offset(xOffset, yOffset),
            end = Offset(xOffset + 1000, yOffset + 1000)
        )

        Box(
            modifier = Modifier.fillMaxSize()
                .background(brush = gradientBrush)
        )
    }
}


suspend fun sendImageToAfter(image: Bitmap, afterService: AfterService): AfterResponse {
    return withContext(Dispatchers.IO) {
        val storageReference = Firebase.storage.reference.child("images") // Sesuaikan dengan path penyimpanan Anda di Firebase Storage

        // Unggah gambar ke Firebase Storage
        val imageFileName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("$imageFileName.jpg")

        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        imageRef.putBytes(byteArray).await()

        // Dapatkan tautan URL gambar yang diunggah
        val imageUrl = imageRef.downloadUrl.await().toString()

        val afterRequest = AfterRequest(
            version = "7de2ea26c616d5bf2245ad0d5e24f0ff9a6204578a5c876db53142edd9d2cd56",
            input = InputData(image = imageUrl)
        )

        val token = "r8_5ZWadHj0PlgY92Na4F513H6BCoXVZYu1XAQXm" // Gantilah dengan token Anda

        // Kirim permintaan ke API Replicate
        var predictionResponse = afterService.sendImageToAfter("Token $token", afterRequest)

        // Simpan ID yang dikembalikan
        val predictionId = predictionResponse.id
        // Tunggu hingga prediksi selesai
        while (predictionResponse.status != "succeeded") {
            predictionResponse = afterService.getPredictionStatus("Token $token", predictionId)
        }

        return@withContext predictionResponse
    }
}


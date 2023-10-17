package com.lugata_ata.unblur_app

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interface untuk mengirim permintaan ke server After.
 */
interface AfterService {
    /**
     * Mengirim gambar ke server After untuk diproses.
     * @param token Token otorisasi untuk mengakses API.
     * @param request Permintaan untuk diproses.
     * @return Respon dari server After.
     */
    @POST("predictions")
    suspend fun sendImageToAfter(@Header("Authorization") token: String, @Body request: AfterRequest): AfterResponse
    
    /**
     * Mendapatkan status prediksi dari server After.
     * @param token Token otorisasi untuk mengakses API.
     * @param predictionId ID prediksi yang ingin dilihat statusnya.
     * @return Respon dari server After.
     */
    @GET("predictions/{prediction_id}")
    suspend fun getPredictionStatus(@Header("Authorization") token: String, @Path("prediction_id") predictionId: String): AfterResponse
}
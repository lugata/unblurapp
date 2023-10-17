package com.lugata_ata.unblur_app

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AfterService {
    @POST("predictions")
    suspend fun sendImageToAfter(@Header("Authorization") token: String, @Body request: AfterRequest): AfterResponse
    @GET("predictions/{prediction_id}")
    suspend fun getPredictionStatus(@Header("Authorization") token: String, @Path("prediction_id") predictionId: String): AfterResponse
}
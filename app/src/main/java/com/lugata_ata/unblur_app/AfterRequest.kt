package com.lugata_ata.unblur_app

data class AfterRequest(
    val version: String,
    val input: InputData
)
data class AfterResponse(
    val id: String,
    val input: InputData,
    val output: String,
    val status: String
)

data class InputData(
    val image: String
)
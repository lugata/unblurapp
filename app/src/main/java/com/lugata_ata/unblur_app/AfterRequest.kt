package com.lugata_ata.unblur_app

/**
 * Kelas data [AfterRequest] merepresentasikan data yang diperlukan untuk melakukan request pada API.
 * Kelas ini memiliki dua properti yaitu [version] dan [input].
 * Properti [version] merepresentasikan versi API yang digunakan.
 * Properti [input] merepresentasikan data input yang dibutuhkan untuk melakukan request.
 * Kelas ini digunakan pada package [com.lugata_ata.unblur_app].
 */
data class AfterRequest(
    val version: String,
    val input: InputData
)

/**
 * Kelas data [AfterResponse] merepresentasikan data yang diperlukan untuk melakukan response dari API.
 * Kelas ini memiliki empat properti yaitu [id], [input], [output], dan [status].
 * Properti [id] merepresentasikan id dari response yang diberikan.
 * Properti [input] merepresentasikan data input yang digunakan pada request.
 * Properti [output] merepresentasikan hasil output dari request.
 * Properti [status] merepresentasikan status dari response yang diberikan.
 * Kelas ini digunakan pada package [com.lugata_ata.unblur_app].
 */
data class AfterResponse(
    val id: String,
    val input: InputData,
    val output: String,
    val status: String
)

/**
 * Kelas data [InputData] merepresentasikan data input yang dibutuhkan untuk melakukan request pada API.
 * Kelas ini memiliki satu properti yaitu [image].
 * Properti [image] merepresentasikan gambar yang akan diolah pada request.
 * Kelas ini digunakan pada package [com.lugata_ata.unblur_app].
 */
data class InputData(
    val image: String
)
package com.example.restaurantreservation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class untuk merepresentasikan data reservasi
 * Mengimplementasikan Parcelable untuk mengirim data antar Activity
 */
@Parcelize
data class Reservation(
    val id: String = "",
    val nama: String = "",
    val jumlahOrang: Int = 0,
    val tanggal: String = "",
    val waktu: String = "",
    val meja: String = "",
    val status: String = "Pending"
) : Parcelable {

    companion object {
        /**
         * Membuat ID unik untuk reservasi berdasarkan timestamp
         */
        fun generateId(): String {
            return "RES_${System.currentTimeMillis()}"
        }
    }
}
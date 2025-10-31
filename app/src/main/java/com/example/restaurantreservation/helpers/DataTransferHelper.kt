package com.example.restaurantreservation.helpers

import android.content.Intent
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.Constants

/**
 * Utility class untuk membantu transfer data antar Activity
 */
object DataTransferHelper {

    /**
     * Extract Reservation dari Intent (multiple ways)
     */
    fun getReservationFromIntent(intent: Intent): Reservation? {
        // Coba ambil sebagai Parcelable object terlebih dahulu (most efficient)
        @Suppress("DEPRECATION")
        val parcelableReservation = intent.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
        if (parcelableReservation != null) {
            return parcelableReservation
        }

        // Jika tidak ada Parcelable, coba buat dari individual fields
        return createReservationFromIndividualFields(intent)
    }

    /**
     * Create Reservation dari individual fields dalam Intent
     */
    private fun createReservationFromIndividualFields(intent: Intent): Reservation? {
        val nama = intent.getStringExtra(Constants.KEY_NAMA) ?: return null
        val jumlahOrang = intent.getIntExtra(Constants.KEY_JUMLAH_ORANG, 0)
        val tanggal = intent.getStringExtra(Constants.KEY_TANGGAL) ?: return null

        return Reservation(
            id = intent.getStringExtra(Constants.KEY_RESERVATION_ID) ?: Reservation.generateId(),
            nama = nama,
            jumlahOrang = jumlahOrang,
            tanggal = tanggal,
            waktu = intent.getStringExtra(Constants.KEY_WAKTU) ?: "",
            meja = intent.getStringExtra(Constants.KEY_MEJA) ?: "",
            catatan = intent.getStringExtra(Constants.KEY_CATATAN) ?: "",
            status = intent.getStringExtra(Constants.KEY_STATUS) ?: "Confirmed",
            createdAt = intent.getLongExtra(Constants.KEY_CREATED_AT, System.currentTimeMillis()),
            updatedAt = intent.getLongExtra(Constants.KEY_UPDATED_AT, System.currentTimeMillis()),
        )
    }

    /**
     * Validasi data sebelum dikirim
     */
    fun validateReservationData(reservation: Reservation): Boolean {
        return reservation.nama.isNotBlank() &&
            reservation.jumlahOrang > 0 &&
            reservation.tanggal.isNotBlank() &&
            reservation.waktu.isNotBlank() &&
            reservation.meja.isNotBlank()
    }

}

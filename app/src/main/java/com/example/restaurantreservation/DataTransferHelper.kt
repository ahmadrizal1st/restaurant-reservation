package com.example.restaurantreservation.utils

import android.content.Intent
import android.os.Bundle
import com.example.restaurantreservation.model.Reservation

/**
 * Utility class untuk membantu transfer data antar Activity
 */
object DataTransferHelper {

    /**
     * Method 1: Mengirim data individual melalui Intent
     */
    fun putReservationData(intent: Intent, reservation: Reservation): Intent {
        return intent.apply {
            // Individual data fields
            putExtra(Constants.KEY_RESERVATION_ID, reservation.id)
            putExtra(Constants.KEY_NAMA, reservation.nama)
            putExtra(Constants.KEY_JUMLAH_ORANG, reservation.jumlahOrang)
            putExtra(Constants.KEY_TANGGAL, reservation.tanggal)
            putExtra(Constants.KEY_WAKTU, reservation.waktu)
            putExtra(Constants.KEY_MEJA, reservation.meja)
            putExtra(Constants.KEY_CATATAN, reservation.catatan)
            putExtra(Constants.KEY_STATUS, reservation.status)
            putExtra(Constants.KEY_CREATED_AT, reservation.createdAt)
            putExtra(Constants.KEY_UPDATED_AT, reservation.updatedAt)

            // Juga kirim sebagai Parcelable object
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
        }
    }

    /**
     * Method 2: Mengirim data individual melalui Bundle
     */
    fun createReservationBundle(reservation: Reservation): Bundle {
        return Bundle().apply {
            putString(Constants.KEY_RESERVATION_ID, reservation.id)
            putString(Constants.KEY_NAMA, reservation.nama)
            putInt(Constants.KEY_JUMLAH_ORANG, reservation.jumlahOrang)
            putString(Constants.KEY_TANGGAL, reservation.tanggal)
            putString(Constants.KEY_WAKTU, reservation.waktu)
            putString(Constants.KEY_MEJA, reservation.meja)
            putString(Constants.KEY_CATATAN, reservation.catatan)
            putString(Constants.KEY_STATUS, reservation.status)
            putLong(Constants.KEY_CREATED_AT, reservation.createdAt)
            putLong(Constants.KEY_UPDATED_AT, reservation.updatedAt)
            putParcelable(Constants.KEY_RESERVATION_DATA, reservation)
        }
    }

    /**
     * Method 3: Extract Reservation dari Intent (multiple ways)
     */
    fun getReservationFromIntent(intent: Intent): Reservation? {
        // Coba ambil sebagai Parcelable object terlebih dahulu (most efficient)
        val parcelableReservation = intent.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
        if (parcelableReservation != null) {
            return parcelableReservation
        }

        // Jika tidak ada Parcelable, coba buat dari individual fields
        return createReservationFromIndividualFields(intent)
    }

    /**
     * Method 4: Create Reservation dari individual fields dalam Intent
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
            updatedAt = intent.getLongExtra(Constants.KEY_UPDATED_AT, System.currentTimeMillis())
        )
    }

    /**
     * Method 5: Extract Reservation dari Bundle
     */
    fun getReservationFromBundle(bundle: Bundle?): Reservation? {
        bundle ?: return null

        // Coba ambil sebagai Parcelable object terlebih dahulu
        val parcelableReservation = bundle.getParcelable<Reservation>(Constants.KEY_RESERVATION_DATA)
        if (parcelableReservation != null) {
            return parcelableReservation
        }

        // Jika tidak ada Parcelable, coba buat dari individual fields
        return createReservationFromBundleFields(bundle)
    }

    /**
     * Method 6: Create Reservation dari individual fields dalam Bundle
     */
    private fun createReservationFromBundleFields(bundle: Bundle): Reservation? {
        val nama = bundle.getString(Constants.KEY_NAMA) ?: return null
        val jumlahOrang = bundle.getInt(Constants.KEY_JUMLAH_ORANG, 0)
        val tanggal = bundle.getString(Constants.KEY_TANGGAL) ?: return null

        return Reservation(
            id = bundle.getString(Constants.KEY_RESERVATION_ID) ?: Reservation.generateId(),
            nama = nama,
            jumlahOrang = jumlahOrang,
            tanggal = tanggal,
            waktu = bundle.getString(Constants.KEY_WAKTU) ?: "",
            meja = bundle.getString(Constants.KEY_MEJA) ?: "",
            catatan = bundle.getString(Constants.KEY_CATATAN) ?: "",
            status = bundle.getString(Constants.KEY_STATUS) ?: "Confirmed",
            createdAt = bundle.getLong(Constants.KEY_CREATED_AT, System.currentTimeMillis()),
            updatedAt = bundle.getLong(Constants.KEY_UPDATED_AT, System.currentTimeMillis())
        )
    }

    /**
     * Method 7: Validasi data sebelum dikirim
     */
    fun validateReservationData(reservation: Reservation): Boolean {
        return reservation.nama.isNotBlank() &&
                reservation.jumlahOrang > 0 &&
                reservation.tanggal.isNotBlank() &&
                reservation.waktu.isNotBlank() &&
                reservation.meja.isNotBlank()
    }

    /**
     * Method 8: Log data transfer untuk debugging
     */
    fun logDataTransfer(source: String, destination: String, reservation: Reservation) {
        println("DATA TRANSFER: $source -> $destination")
        println("Reservation Data: $reservation")
        println("Data Size: ${reservation.toString().length} characters")
    }
}
package com.example.restaurantreservation.utils

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.restaurantreservation.model.Reservation

/**
 * Utility class untuk membantu menerima data dari Activity lain
 */
object DataReceiverHelper {

    /**
     * Method 1: Extract Reservation dari Intent dengan multiple fallbacks
     */
    fun getReservationFromIntent(intent: Intent, context: android.content.Context): Reservation? {
        return try {
            // Priority 1: Parcelable object (most efficient)
            val parcelableReservation = intent.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
            if (parcelableReservation != null) {
                logDataReceipt("Parcelable Object", parcelableReservation)
                return parcelableReservation
            }

            // Priority 2: Individual fields
            val individualReservation = createReservationFromIndividualFields(intent)
            if (individualReservation != null) {
                logDataReceipt("Individual Fields", individualReservation)
                return individualReservation
            }

            // Priority 3: Bundle
            val bundleReservation = getReservationFromBundle(intent.extras)
            if (bundleReservation != null) {
                logDataReceipt("Bundle", bundleReservation)
                return bundleReservation
            }

            // Data tidak valid
            showDataError(context, "Tidak ada data reservasi yang valid")
            null

        } catch (e: Exception) {
            showDataError(context, "Error menerima data: ${e.message}")
            null
        }
    }

    /**
     * Method 2: Create Reservation dari individual fields
     */
    private fun createReservationFromIndividualFields(intent: Intent): Reservation? {
        return try {
            val nama = intent.getStringExtra(Constants.KEY_NAMA)
            val jumlahOrang = intent.getIntExtra(Constants.KEY_JUMLAH_ORANG, -1)
            val tanggal = intent.getStringExtra(Constants.KEY_TANGGAL)

            // Validasi field wajib
            if (nama.isNullOrEmpty() || jumlahOrang == -1 || tanggal.isNullOrEmpty()) {
                return null
            }

            Reservation(
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
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Method 3: Extract Reservation dari Bundle
     */
    fun getReservationFromBundle(bundle: Bundle?): Reservation? {
        bundle ?: return null

        return try {
            // Coba sebagai Parcelable
            val parcelableReservation = bundle.getParcelable<Reservation>(Constants.KEY_RESERVATION_DATA)
            if (parcelableReservation != null) return parcelableReservation

            // Coba dari individual fields dalam bundle
            val nama = bundle.getString(Constants.KEY_NAMA)
            val jumlahOrang = bundle.getInt(Constants.KEY_JUMLAH_ORANG, -1)
            val tanggal = bundle.getString(Constants.KEY_TANGGAL)

            if (nama.isNullOrEmpty() || jumlahOrang == -1 || tanggal.isNullOrEmpty()) {
                return null
            }

            Reservation(
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
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Method 4: Validasi data yang diterima
     */
    fun validateReceivedData(reservation: Reservation): ValidationResult {
        return when {
            reservation.nama.isBlank() -> ValidationResult.Error("Nama tidak boleh kosong")
            reservation.jumlahOrang < 1 -> ValidationResult.Error("Jumlah orang tidak valid")
            reservation.tanggal.isBlank() -> ValidationResult.Error("Tanggal tidak valid")
            reservation.waktu.isBlank() -> ValidationResult.Error("Waktu tidak valid")
            reservation.meja.isBlank() -> ValidationResult.Error("Meja tidak valid")
            else -> ValidationResult.Success
        }
    }

    /**
     * Method 5: Get action dari Intent
     */
    fun getActionFromIntent(intent: Intent): String {
        return intent.getStringExtra(Constants.KEY_ACTION) ?: Constants.ACTION_VIEW
    }

    /**
     * Method 6: Parse additional parameters
     */
    fun getAdditionalParams(intent: Intent): Map<String, Any> {
        return mapOf(
            "source" to intent.getStringExtra("source") ?: "unknown",
            "timestamp" to intent.getLongExtra("timestamp", System.currentTimeMillis()),
            "version" to intent.getIntExtra("data_version", 1)
        )
    }

    /**
     * Method 7: Log data receipt untuk debugging
     */
    private fun logDataReceipt(method: String, reservation: Reservation) {
        println("DATA RECEIVED - Method: $method")
        println("Reservation ID: ${reservation.id}")
        println("Nama: ${reservation.nama}")
        println("Jumlah Orang: ${reservation.jumlahOrang}")
        println("Tanggal: ${reservation.tanggal}")
        println("Data Size: ${reservation.toString().length} bytes")
        println("---")
    }

    /**
     * Method 8: Show error message
     */
    private fun showDataError(context: android.content.Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        println("DATA RECEIVE ERROR: $message")
    }

    /**
     * Method 9: Check if intent contains reservation data
     */
    fun hasReservationData(intent: Intent): Boolean {
        return intent.hasExtra(Constants.KEY_RESERVATION_DATA) ||
                (intent.hasExtra(Constants.KEY_NAMA) &&
                        intent.hasExtra(Constants.KEY_JUMLAH_ORANG) &&
                        intent.hasExtra(Constants.KEY_TANGGAL))
    }

    /**
     * Method 10: Get reservation with default values
     */
    fun getReservationWithDefaults(intent: Intent): Reservation {
        return getReservationFromIntent(intent, android.content.Context()) ?:
        Reservation.create(
            nama = "Guest",
            jumlahOrang = 2,
            tanggal = "01/01/2024",
            waktu = "12:00",
            meja = "Meja 1",
            status = "Unknown"
        )
    }
}
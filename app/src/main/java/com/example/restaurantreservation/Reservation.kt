package com.example.restaurantreservation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

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
    val catatan: String = "",
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    companion object {
        /**
         * Membuat ID unik untuk reservasi berdasarkan timestamp
         */
        fun generateId(): String {
            return "RES_${System.currentTimeMillis()}"
        }

        /**
         * Create Reservation from individual data
         */
        fun create(
            nama: String,
            jumlahOrang: Int,
            tanggal: String,
            waktu: String,
            meja: String,
            catatan: String = "",
            status: String = "Confirmed"
        ): Reservation {
            return Reservation(
                id = generateId(),
                nama = nama,
                jumlahOrang = jumlahOrang,
                tanggal = tanggal,
                waktu = waktu,
                meja = meja,
                catatan = catatan,
                status = status
            )
        }
    }

    /**
     * Get formatted date for display
     */
    fun getFormattedDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(tanggal)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            tanggal
        }
    }

    /**
     * Get formatted time for display
     */
    fun getFormattedTime(): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = inputFormat.parse(waktu)
            outputFormat.format(time ?: Date())
        } catch (e: Exception) {
            waktu
        }
    }

    /**
     * Get creation date as formatted string
     */
    fun getCreatedAtFormatted(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date(createdAt))
    }

    /**
     * Convert to bundle-friendly map
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "nama" to nama,
            "jumlahOrang" to jumlahOrang,
            "tanggal" to tanggal,
            "waktu" to waktu,
            "meja" to meja,
            "catatan" to catatan,
            "status" to status,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    /**
     * Create copy with updated fields
     */
    fun update(
        nama: String? = null,
        jumlahOrang: Int? = null,
        tanggal: String? = null,
        waktu: String? = null,
        meja: String? = null,
        catatan: String? = null,
        status: String? = null
    ): Reservation {
        return this.copy(
            nama = nama ?: this.nama,
            jumlahOrang = jumlahOrang ?: this.jumlahOrang,
            tanggal = tanggal ?: this.tanggal,
            waktu = waktu ?: this.waktu,
            meja = meja ?: this.meja,
            catatan = catatan ?: this.catatan,
            status = status ?: this.status,
            updatedAt = System.currentTimeMillis()
        )
    }

    override fun toString(): String {
        return "Reservation(id='$id', nama='$nama', jumlahOrang=$jumlahOrang, tanggal='$tanggal', waktu='$waktu', meja='$meja', status='$status')"
    }
}
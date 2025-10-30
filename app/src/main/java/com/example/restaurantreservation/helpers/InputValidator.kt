package com.example.restaurantreservation.utils

import java.text.SimpleDateFormat
import java.util.*

object InputValidator {
    /**
     * Validasi nama pemesan
     * @param nama Nama yang akan divalidasi
     * @return Result validasi (success/error)
     */
    fun validateNama(nama: String): ValidationResult {
        return when {
            nama.isEmpty() -> ValidationResult.Error("Nama harus diisi!")
            nama.length < 3 -> ValidationResult.Error("Nama minimal 3 karakter!")
            nama.length > 50 -> ValidationResult.Error("Nama maksimal 50 karakter!")
            !isValidNameFormat(nama) -> ValidationResult.Error("Nama hanya boleh mengandung huruf dan spasi!")
            else -> ValidationResult.Success
        }
    }

    /**
     * Validasi jumlah orang
     * @param jumlah Jumlah orang yang akan divalidasi
     * @return Result validasi (success/error)
     */
    fun validateJumlahOrang(jumlah: Int): ValidationResult {
        return when {
            jumlah < 1 -> ValidationResult.Error("Jumlah orang minimal 1!")
            jumlah > 50 -> ValidationResult.Error("Jumlah orang maksimal 50!")
            else -> ValidationResult.Success
        }
    }

    /**
     * Validasi tanggal reservasi
     * @param tanggal String tanggal dalam format dd/MM/yyyy
     * @return Result validasi (success/error)
     */
    fun validateTanggal(tanggal: String): ValidationResult {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.isLenient = false
            val reservationDate = dateFormat.parse(tanggal)
            val today = Calendar.getInstance()
            val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }

            reservationDate?.let { date ->
                val calDate = Calendar.getInstance().apply { time = date }

                when {
                    calDate.before(today) -> ValidationResult.Error("Tanggal tidak boleh di masa lalu!")
                    calDate.after(maxDate) -> ValidationResult.Error("Tanggal tidak boleh lebih dari 1 tahun ke depan!")
                    else -> ValidationResult.Success
                }
            } ?: ValidationResult.Error("Format tanggal tidak valid!")
        } catch (e: Exception) {
            ValidationResult.Error("Tanggal tidak valid!")
        }
    }

    /**
     * Format nama hanya boleh huruf dan spasi
     */
    private fun isValidNameFormat(nama: String): Boolean {
        val regex = "^[a-zA-Z\\s]+$".toRegex()
        return regex.matches(nama)
    }

    /**
     * Validasi semua input sekaligus
     */
    fun validateAll(
        nama: String,
        jumlah: Int,
        tanggal: String,
    ): ValidationResult {
        return when {
            validateNama(nama) is ValidationResult.Error -> validateNama(nama)
            validateJumlahOrang(jumlah) is ValidationResult.Error -> validateJumlahOrang(jumlah)
            validateTanggal(tanggal) is ValidationResult.Error -> validateTanggal(tanggal)
            else -> ValidationResult.Success
        }
    }
}

/**
 * Sealed class untuk menangani result validasi
 */
sealed class ValidationResult {
    object Success : ValidationResult()

    data class Error(val message: String) : ValidationResult()
}

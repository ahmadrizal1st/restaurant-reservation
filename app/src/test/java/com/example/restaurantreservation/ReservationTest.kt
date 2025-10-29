package com.example.restaurantreservation

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.InputValidator
import com.example.restaurantreservation.utils.ValidationResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unit tests untuk model Reservation dan utility classes
 *
 * @author Restaurant Reservation Team
 * @since 1.0
 */
class ReservationTest {

    private lateinit var validReservation: Reservation
    private lateinit var invalidReservation: Reservation

    @Before
    fun setUp() {
        // Setup test data sebelum setiap test
        validReservation = Reservation.create(
            nama = "John Doe",
            jumlahOrang = 4,
            tanggal = "15/12/2024",
            waktu = "19:00",
            meja = "Meja VIP 1",
            catatan = "Test reservation"
        )

        invalidReservation = Reservation.create(
            nama = "",
            jumlahOrang = 0,
            tanggal = "",
            waktu = "",
            meja = ""
        )
    }

    @Test
    fun testReservationCreation() {
        // Test pembuatan reservasi valid
        assertEquals("John Doe", validReservation.nama)
        assertEquals(4, validReservation.jumlahOrang)
        assertEquals("15/12/2024", validReservation.tanggal)
        assertEquals("19:00", validReservation.waktu)
        assertEquals("Meja VIP 1", validReservation.meja)
        assertEquals("Test reservation", validReservation.catatan)
        assertEquals("Confirmed", validReservation.status)
    }

    @Test
    fun testReservationIdGeneration() {
        // Test ID generation
        val reservation1 = Reservation.create(
            nama = "Test 1",
            jumlahOrang = 2,
            tanggal = "01/01/2024",
            waktu = "12:00",
            meja = "Meja 1"
        )

        Thread.sleep(1) // Ensure different timestamps

        val reservation2 = Reservation.create(
            nama = "Test 2",
            jumlahOrang = 3,
            tanggal = "01/01/2024",
            waktu = "13:00",
            meja = "Meja 2"
        )

        assertNotEquals(reservation1.id, reservation2.id)
        assertTrue(reservation1.id.startsWith("RES_"))
        assertTrue(reservation2.id.startsWith("RES_"))
    }

    @Test
    fun testReservationUpdate() {
        // Test update reservasi
        val updatedReservation = validReservation.update(
            nama = "Jane Doe",
            jumlahOrang = 6,
            status = "Updated"
        )

        assertEquals("Jane Doe", updatedReservation.nama)
        assertEquals(6, updatedReservation.jumlahOrang)
        assertEquals("Updated", updatedReservation.status)
        assertEquals(validReservation.tanggal, updatedReservation.tanggal) // Tanggal tetap sama
        assertTrue(updatedReservation.updatedAt >= validReservation.updatedAt)
    }

    @Test
    fun testReservationFormattedDate() {
        // Test date formatting
        val formattedDate = validReservation.getFormattedDate()
        assertTrue(formattedDate.contains("Desember") || formattedDate.contains("December"))
    }

    @Test
    fun testReservationFormattedTime() {
        // Test time formatting
        val formattedTime = validReservation.getFormattedTime()
        assertEquals("19:00", formattedTime)
    }

    @Test
    fun testInputValidatorValidNama() {
        // Test validasi nama yang valid
        val result = InputValidator.validateNama("John Doe")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun testInputValidatorEmptyNama() {
        // Test validasi nama kosong
        val result = InputValidator.validateNama("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Nama harus diisi!", (result as ValidationResult.Error).message)
    }

    @Test
    fun testInputValidatorShortNama() {
        // Test validasi nama terlalu pendek
        val result = InputValidator.validateNama("Jo")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Nama minimal 3 karakter!", (result as ValidationResult.Error).message)
    }

    @Test
    fun testInputValidatorLongNama() {
        // Test validasi nama terlalu panjang
        val longName = "A".repeat(51)
        val result = InputValidator.validateNama(longName)
        assertTrue(result is ValidationResult.Error)
        assertEquals("Nama maksimal 50 karakter!", (result as ValidationResult.Error).message)
    }

    @Test
    fun testInputValidatorInvalidNamaFormat() {
        // Test validasi format nama tidak valid
        val result = InputValidator.validateNama("John123")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Nama hanya boleh mengandung huruf dan spasi!", (result as ValidationResult.Error).message)
    }

    @Test
    fun testInputValidatorValidJumlahOrang() {
        // Test validasi jumlah orang valid
        val result = InputValidator.validateJumlahOrang(4)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun testInputValidatorInvalidJumlahOrang() {
        // Test validasi jumlah orang tidak valid
        val result1 = InputValidator.validateJumlahOrang(0)
        val result2 = InputValidator.validateJumlahOrang(51)

        assertTrue(result1 is ValidationResult.Error)
        assertTrue(result2 is ValidationResult.Error)
    }

    @Test
    fun testInputValidatorValidTanggal() {
        // Test validasi tanggal valid
        val tomorrow = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time)

        val result = InputValidator.validateTanggal(tomorrow)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun testInputValidatorPastTanggal() {
        // Test validasi tanggal masa lalu
        val yesterday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time)

        val result = InputValidator.validateTanggal(yesterday)
        assertTrue(result is ValidationResult.Error)
        assertEquals("Tanggal tidak boleh di masa lalu!", (result as ValidationResult.Error).message)
    }

    @Test
    fun testReservationToMap() {
        // Test konversi reservasi ke Map
        val map = validReservation.toMap()

        assertEquals(validReservation.id, map["id"])
        assertEquals(validReservation.nama, map["nama"])
        assertEquals(validReservation.jumlahOrang.toString(), map["jumlahOrang"])
        assertEquals(validReservation.tanggal, map["tanggal"])
        assertEquals(validReservation.waktu, map["waktu"])
        assertEquals(validReservation.meja, map["meja"])
        assertEquals(validReservation.catatan, map["catatan"])
        assertEquals(validReservation.status, map["status"])
    }

    @Test
    fun testReservationToString() {
        // Test string representation
        val stringRepresentation = validReservation.toString()
        assertTrue(stringRepresentation.contains("Reservation"))
        assertTrue(stringRepresentation.contains(validReservation.nama))
        assertTrue(stringRepresentation.contains(validReservation.jumlahOrang.toString()))
    }
}
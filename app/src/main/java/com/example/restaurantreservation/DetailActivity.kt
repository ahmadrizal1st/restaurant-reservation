package com.example.restaurantreservation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.IntentUtils

class DetailActivity : AppCompatActivity() {

    private lateinit var tvNama: TextView
    private lateinit var tvJumlahOrang: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var tvMeja: TextView
    private lateinit var tvStatus: TextView

    private lateinit var btnBukaMaps: Button
    private lateinit var btnTeleponRestoran: Button
    private lateinit var btnBagikanReservasi: Button
    private lateinit var btnBukaWebsite: Button
    private lateinit var btnKirimEmail: Button
    private lateinit var btnBukaKalender: Button
    private lateinit var btnWhatsApp: Button
    private lateinit var btnKembali: Button

    private lateinit var reservation: Reservation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        terimaDataDariIntent()
        tampilkanDataReservasi()
        setupClickListeners()
    }

    private fun initViews() {
        tvNama = findViewById(R.id.tvNama)
        tvJumlahOrang = findViewById(R.id.tvJumlahOrang)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvWaktu = findViewById(R.id.tvWaktu)
        tvMeja = findViewById(R.id.tvMeja)
        tvStatus = findViewById(R.id.tvStatus)

        btnBukaMaps = findViewById(R.id.btnBukaMaps)
        btnTeleponRestoran = findViewById(R.id.btnTeleponRestoran)
        btnBagikanReservasi = findViewById(R.id.btnBagikanReservasi)
        btnBukaWebsite = findViewById(R.id.btnBukaWebsite)
        btnKirimEmail = findViewById(R.id.btnKirimEmail)
        btnBukaKalender = findViewById(R.id.btnBukaKalender)
        btnWhatsApp = findViewById(R.id.btnWhatsApp)
        btnKembali = findViewById(R.id.btnKembali)
    }

    private fun terimaDataDariIntent() {
        // Coba ambil data sebagai Parcelable object terlebih dahulu
        reservation = intent.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA) ?: run {
            // Jika tidak ada Parcelable, ambil data individual
            val nama = intent.getStringExtra(Constants.KEY_NAMA) ?: "Tidak tersedia"
            val jumlahOrang = intent.getIntExtra(Constants.KEY_JUMLAH_ORANG, 0)
            val tanggal = intent.getStringExtra(Constants.KEY_TANGGAL) ?: "Tidak tersedia"
            val waktu = intent.getStringExtra(Constants.KEY_WAKTU) ?: "Tidak tersedia"
            val meja = intent.getStringExtra(Constants.KEY_MEJA) ?: "Tidak tersedia"

            Reservation(
                id = Reservation.generateId(),
                nama = nama,
                jumlahOrang = jumlahOrang,
                tanggal = tanggal,
                waktu = waktu,
                meja = meja,
                status = "Confirmed"
            )
        }
    }

    private fun tampilkanDataReservasi() {
        tvNama.text = reservation.nama
        tvJumlahOrang.text = "${reservation.jumlahOrang} orang"
        tvTanggal.text = reservation.tanggal
        tvWaktu.text = reservation.waktu
        tvMeja.text = reservation.meja
        tvStatus.text = reservation.status

        // Set warna status berdasarkan status reservasi
        when (reservation.status.toLowerCase()) {
            "confirmed" -> tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            "pending" -> tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark))
            "cancelled" -> tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            else -> tvStatus.setTextColor(getColor(android.R.color.black))
        }
    }

    private fun setupClickListeners() {
        // === IMPLICIT INTENT LISTENERS ===

        // 1. Buka Google Maps
        btnBukaMaps.setOnClickListener {
            IntentUtils.openMaps(this)
        }

        // 2. Telepon Restoran
        btnTeleponRestoran.setOnClickListener {
            IntentUtils.callRestaurant(this)
        }

        // 3. Bagikan Reservasi
        btnBagikanReservasi.setOnClickListener {
            IntentUtils.shareReservation(this, reservation)
        }

        // 4. Buka Website
        btnBukaWebsite.setOnClickListener {
            IntentUtils.openWebsite(this)
        }

        // 5. Kirim Email Konfirmasi
        btnKirimEmail.setOnClickListener {
            IntentUtils.sendConfirmationEmail(this, reservation)
        }

        // 6. Tambahkan ke Kalender
        btnBukaKalender.setOnClickListener {
            IntentUtils.addToCalendar(this, reservation)
        }

        // 7. Buka WhatsApp
        btnWhatsApp.setOnClickListener {
            IntentUtils.openWhatsApp(this, reservation)
        }

        // 8. Tombol Kembali (Explicit Intent)
        btnKembali.setOnClickListener {
            finish() // Kembali ke MainActivity
        }
    }

    /**
     * Method untuk handle result dari implicit intent (jika diperlukan)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Bisa digunakan jika perlu handle result dari aplikasi lain
    }
}
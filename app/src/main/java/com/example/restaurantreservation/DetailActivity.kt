package com.example.restaurantreservation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var tvNama: TextView
    private lateinit var tvJumlahOrang: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var btnBukaMaps: Button
    private lateinit var btnKembali: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        tampilkanDataReservasi()
        setupClickListeners()
    }

    private fun initViews() {
        tvNama = findViewById(R.id.tvNama)
        tvJumlahOrang = findViewById(R.id.tvJumlahOrang)
        tvTanggal = findViewById(R.id.tvTanggal)
        btnBukaMaps = findViewById(R.id.btnBukaMaps)
        btnKembali = findViewById(R.id.btnKembali)
    }

    private fun tampilkanDataReservasi() {
        // Ambil data dari Intent
        val nama = intent.getStringExtra("NAMA") ?: "Tidak tersedia"
        val jumlahOrang = intent.getIntExtra("JUMLAH_ORANG", 0)
        val tanggal = intent.getStringExtra("TANGGAL") ?: "Tidak tersedia"

        // Tampilkan data
        tvNama.text = nama
        tvJumlahOrang.text = jumlahOrang.toString()
        tvTanggal.text = tanggal
    }

    private fun setupClickListeners() {
        // Tombol Buka Maps
        btnBukaMaps.setOnClickListener {
            bukaGoogleMaps()
        }

        // Tombol Kembali
        btnKembali.setOnClickListener {
            finish() // Kembali ke MainActivity
        }
    }

    private fun bukaGoogleMaps() {
        // Koordinat contoh restoran (bisa diganti)
        val gmmIntentUri = Uri.parse("geo:-6.2088,106.8456?q=restaurant")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // Cek apakah Google Maps terinstall
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback ke browser jika Maps tidak terinstall
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/restaurant"))
            startActivity(webIntent)
        }
    }
}
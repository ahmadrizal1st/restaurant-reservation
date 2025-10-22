package com.example.restaurantreservation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etNama: EditText
    private lateinit var npJumlahOrang: NumberPicker
    private lateinit var btnBuatReservasi: Button
    private lateinit var btnLihatDaftar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen UI
        initViews()

        // Setup NumberPicker
        setupNumberPicker()

        // Setup click listeners
        setupClickListeners()
    }

    private fun initViews() {
        etNama = findViewById(R.id.etNama)
        npJumlahOrang = findViewById(R.id.npJumlahOrang)
        btnBuatReservasi = findViewById(R.id.btnBuatReservasi)
        btnLihatDaftar = findViewById(R.id.btnLihatDaftar)
    }

    private fun setupNumberPicker() {
        // Set range jumlah orang 1-20
        npJumlahOrang.minValue = 1
        npJumlahOrang.maxValue = 20
        npJumlahOrang.value = 2 // default value
    }

    private fun setupClickListeners() {
        // Tombol Buat Reservasi
        btnBuatReservasi.setOnClickListener {
            buatReservasi()
        }

        // Tombol Lihat Daftar
        btnLihatDaftar.setOnClickListener {
            lihatDaftarReservasi()
        }
    }

    private fun buatReservasi() {
        val nama = etNama.text.toString().trim()

        // Validasi input
        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahOrang = npJumlahOrang.value
        val tanggal = getCurrentDate()

        // Intent ke DetailActivity
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("NAMA", nama)
            putExtra("JUMLAH_ORANG", jumlahOrang)
            putExtra("TANGGAL", tanggal)
        }

        startActivity(intent)

        // Reset form
        etNama.text.clear()
        npJumlahOrang.value = 2
    }

    private fun lihatDaftarReservasi() {
        // Intent ke ListActivity
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
package com.example.restaurantreservation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ListActivity : AppCompatActivity() {

    private lateinit var tvEmptyList: TextView
    private lateinit var btnTambahReservasi: Button
    private lateinit var btnKembaliKeUtama: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        initViews()
        setupClickListeners()

        // Untuk sementara tampilkan pesan empty list
        // Nanti akan diimplementasi dengan RecyclerView
        tampilkanEmptyList()
    }

    private fun initViews() {
        tvEmptyList = findViewById(R.id.tvEmptyList)
        btnTambahReservasi = findViewById(R.id.btnTambahReservasi)
        btnKembaliKeUtama = findViewById(R.id.btnKembaliKeUtama)
    }

    private fun setupClickListeners() {
        // Tombol Tambah Reservasi
        btnTambahReservasi.setOnClickListener {
            kembaliKeMainActivity()
        }

        // Tombol Kembali ke Utama
        btnKembaliKeUtama.setOnClickListener {
            finish() // Kembali ke MainActivity
        }
    }

    private fun tampilkanEmptyList() {
        tvEmptyList.text = "Belum ada reservasi.\nSilakan buat reservasi terlebih dahulu."
    }

    private fun kembaliKeMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}
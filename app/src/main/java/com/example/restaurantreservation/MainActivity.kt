package com.example.restaurantreservation

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.restaurantreservation.utils.InputValidator
import com.example.restaurantreservation.utils.ValidationResult
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etNama: EditText
    private lateinit var npJumlahOrang: NumberPicker
    private lateinit var tvTanggal: TextView
    private lateinit var btnPilihTanggal: Button
    private lateinit var btnBuatReservasi: Button
    private lateinit var btnLihatDaftar: Button
    private lateinit var textInputLayoutNama: com.google.android.material.textfield.TextInputLayout

    // Variabel untuk menyimpan tanggal
    private var selectedDate: String = ""
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen UI
        initViews()

        // Setup komponen
        setupNumberPicker()
        setupDatePicker()
        setupClickListeners()
        setupTextWatchers()

        // Set tanggal default (hari ini)
        setDefaultDate()
    }

    private fun initViews() {
        etNama = findViewById(R.id.etNama)
        npJumlahOrang = findViewById(R.id.npJumlahOrang)
        tvTanggal = findViewById(R.id.tvTanggal)
        btnPilihTanggal = findViewById(R.id.btnPilihTanggal)
        btnBuatReservasi = findViewById(R.id.btnBuatReservasi)
        btnLihatDaftar = findViewById(R.id.btnLihatDaftar)
        textInputLayoutNama = findViewById(R.id.textInputLayoutNama)
    }

    private fun setupNumberPicker() {
        // Set range jumlah orang 1-50
        npJumlahOrang.minValue = 1
        npJumlahOrang.maxValue = 50
        npJumlahOrang.value = 2 // default value

        // Custom formatter untuk menampilkan "orang"
        npJumlahOrang.setFormatter { value -> "$value orang" }

        // Set listener untuk real-time validation
        npJumlahOrang.setOnValueChangedListener { _, _, newVal ->
            validateJumlahOrang(newVal)
        }
    }

    private fun setupDatePicker() {
        // Setup date picker dialog
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateDisplay()
            validateTanggal(selectedDate)
        }

        btnPilihTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                // Set min date (hari ini)
                datePicker.minDate = System.currentTimeMillis() - 1000

                // Set max date (1 tahun dari sekarang)
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
                datePicker.maxDate = maxDate.timeInMillis

                // Set title
                setTitle("Pilih Tanggal Reservasi")
            }.show()
        }
    }

    private fun setupClickListeners() {
        // Tombol Buat Reservasi dengan validasi lengkap
        btnBuatReservasi.setOnClickListener {
            buatReservasiDenganValidasi()
        }

        // Tombol Lihat Daftar
        btnLihatDaftar.setOnClickListener {
            lihatDaftarReservasi()
        }
    }

    private fun setupTextWatchers() {
        // Real-time validation untuk nama
        etNama.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                validateNama(s.toString())
            }
        })
    }

    private fun buatReservasiDenganValidasi() {
        val nama = etNama.text.toString().trim()
        val jumlahOrang = npJumlahOrang.value

        // Validasi semua input
        when (val result = InputValidator.validateAll(nama, jumlahOrang, selectedDate)) {
            is ValidationResult.Success -> {
                // Semua validasi passed, buat reservasi
                buatReservasi(nama, jumlahOrang, selectedDate)
            }
            is ValidationResult.Error -> {
                // Tampilkan error message
                showError(result.message)
            }
        }
    }

    private fun buatReservasi(nama: String, jumlahOrang: Int, tanggal: String) {
        // Intent ke DetailActivity
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("NAMA", nama)
            putExtra("JUMLAH_ORANG", jumlahOrang)
            putExtra("TANGGAL", tanggal)
        }

        startActivity(intent)

        // Tampilkan success message
        Toast.makeText(this, "Reservasi berhasil dibuat untuk $nama!", Toast.LENGTH_LONG).show()

        // Reset form
        resetForm()
    }

    private fun lihatDaftarReservasi() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    // === VALIDATION METHODS ===

    private fun validateNama(nama: String) {
        when (val result = InputValidator.validateNama(nama)) {
            is ValidationResult.Success -> {
                textInputLayoutNama.error = null
                textInputLayoutNama.isErrorEnabled = false
            }
            is ValidationResult.Error -> {
                textInputLayoutNama.error = result.message
            }
        }
    }

    private fun validateJumlahOrang(jumlah: Int) {
        when (val result = InputValidator.validateJumlahOrang(jumlah)) {
            is ValidationResult.Success -> {
                // No error, continue
            }
            is ValidationResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateTanggal(tanggal: String) {
        when (val result = InputValidator.validateTanggal(tanggal)) {
            is ValidationResult.Success -> {
                // No error, continue
            }
            is ValidationResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // === HELPER METHODS ===

    private fun setDefaultDate() {
        selectedDate = getCurrentDate()
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvTanggal.text = displayFormat.format(calendar.time)

        // Simpan dalam format dd/MM/yyyy untuk data
        val dataFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dataFormat.format(calendar.time)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun resetForm() {
        etNama.text.clear()
        npJumlahOrang.value = 2
        setDefaultDate()
        textInputLayoutNama.error = null
        textInputLayoutNama.isErrorEnabled = false
    }
}
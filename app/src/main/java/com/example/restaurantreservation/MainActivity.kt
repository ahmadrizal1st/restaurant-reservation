package com.example.restaurantreservation

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.InputValidator
import com.example.restaurantreservation.utils.ValidationResult
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etNama: EditText
    private lateinit var npJumlahOrang: NumberPicker
    private lateinit var tvTanggal: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var spMeja: Spinner
    private lateinit var btnPilihTanggal: Button
    private lateinit var btnPilihWaktu: Button
    private lateinit var btnBuatReservasi: Button
    private lateinit var btnLihatDaftar: Button
    private lateinit var textInputLayoutNama: com.google.android.material.textfield.TextInputLayout

    // Variabel untuk menyimpan data
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedMeja: String = ""
    private val calendar = Calendar.getInstance()

    // Data untuk spinner meja
    private val listMeja = arrayOf("Pilih Meja", "Meja 1", "Meja 2", "Meja 3", "Meja 4", "Meja 5", "Meja VIP 1", "Meja VIP 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen UI
        initViews()

        // Setup komponen
        setupNumberPicker()
        setupSpinnerMeja()
        setupDatePicker()
        setupTimePicker()
        setupClickListeners()
        setupTextWatchers()

        // Set default values
        setDefaultDateTime()

        // Handle intent dari activity lain (jika ada)
        handleIncomingIntent()
    }

    private fun initViews() {
        etNama = findViewById(R.id.etNama)
        npJumlahOrang = findViewById(R.id.npJumlahOrang)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvWaktu = findViewById(R.id.tvWaktu)
        spMeja = findViewById(R.id.spMeja)
        btnPilihTanggal = findViewById(R.id.btnPilihTanggal)
        btnPilihWaktu = findViewById(R.id.btnPilihWaktu)
        btnBuatReservasi = findViewById(R.id.btnBuatReservasi)
        btnLihatDaftar = findViewById(R.id.btnLihatDaftar)
        textInputLayoutNama = findViewById(R.id.textInputLayoutNama)
    }

    private fun setupNumberPicker() {
        npJumlahOrang.minValue = 1
        npJumlahOrang.maxValue = 50
        npJumlahOrang.value = 2
        npJumlahOrang.setFormatter { value -> "$value orang" }

        npJumlahOrang.setOnValueChangedListener { _, _, newVal ->
            validateJumlahOrang(newVal)
        }
    }

    private fun setupSpinnerMeja() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMeja)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMeja.adapter = adapter

        // Listener untuk spinner
        spMeja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedMeja = if (position > 0) listMeja[position] else ""
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMeja = ""
            }
        }
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateDisplay()
        }

        btnPilihTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
                datePicker.maxDate = maxDate.timeInMillis
                setTitle("Pilih Tanggal Reservasi")
            }.show()
        }
    }

    private fun setupTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeDisplay()
        }

        btnPilihWaktu.setOnClickListener {
            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).apply {
                setTitle("Pilih Waktu Reservasi")
            }.show()
        }
    }

    private fun setupClickListeners() {
        btnBuatReservasi.setOnClickListener {
            buatReservasiDenganValidasi()
        }

        btnLihatDaftar.setOnClickListener {
            lihatDaftarReservasi()
        }
    }

    private fun setupTextWatchers() {
        etNama.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                validateNama(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // === EXPLICIT INTENT METHODS ===

    /**
     * Method 1: Explicit Intent dengan mengirim data individual
     */
    private fun buatReservasiDenganValidasi() {
        val nama = etNama.text.toString().trim()
        val jumlahOrang = npJumlahOrang.value

        if (!isFormValid(nama)) return

        // METHOD A: Explicit Intent dengan putExtra individual
        val intent = Intent(this, DetailActivity::class.java).apply {
            // Mengirim data sebagai individual extras
            putExtra(Constants.KEY_NAMA, nama)
            putExtra(Constants.KEY_JUMLAH_ORANG, jumlahOrang)
            putExtra(Constants.KEY_TANGGAL, selectedDate)
            putExtra(Constants.KEY_WAKTU, selectedTime)
            putExtra(Constants.KEY_MEJA, selectedMeja)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        }

        // Start activity dengan explicit intent
        startActivity(intent)

        showSuccessMessage("Reservasi berhasil dibuat untuk $nama!")
        resetForm()
    }

    /**
     * Method 2: Explicit Intent dengan mengirim Parcelable object
     */
    private fun buatReservasiDenganParcelable(nama: String, jumlahOrang: Int) {
        // Buat object Reservation
        val reservation = Reservation(
            id = Reservation.generateId(),
            nama = nama,
            jumlahOrang = jumlahOrang,
            tanggal = selectedDate,
            waktu = selectedTime,
            meja = selectedMeja,
            status = "Confirmed"
        )

        // METHOD B: Explicit Intent dengan Parcelable object
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        }

        startActivity(intent)
        showSuccessMessage("Reservasi berhasil dibuat!")
        resetForm()
    }

    /**
     * Method 3: Explicit Intent untuk navigasi sederhana (tanpa data)
     */
    private fun lihatDaftarReservasi() {
        // Explicit Intent tanpa mengirim data
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    /**
     * Method 4: Explicit Intent dengan startActivityForResult (jika butuh result kembali)
     */
    private fun bukaDetailUntukEdit(reservation: Reservation) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_EDIT)
        }

        // Start activity dan tunggu result
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_RESERVATION)
    }

    /**
     * Handle result dari activity lain
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_CODE_EDIT_RESERVATION -> {
                if (resultCode == RESULT_OK) {
                    val updatedReservation = data?.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
                    updatedReservation?.let {
                        showSuccessMessage("Reservasi berhasil diupdate!")
                    }
                }
            }
        }
    }

    /**
     * Handle incoming intent dari activity lain
     */
    private fun handleIncomingIntent() {
        // Cek jika ada data yang dikirim ke MainActivity
        when (intent.action) {
            Intent.ACTION_EDIT -> {
                // Handle edit action dari activity lain
                val reservation = intent.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
                reservation?.let { prefillForm(it) }
            }
        }
    }

    // === HELPER METHODS ===

    private fun isFormValid(nama: String): Boolean {
        return when (InputValidator.validateNama(nama)) {
            is ValidationResult.Success -> true
            is ValidationResult.Error -> {
                showError("Harap isi form dengan benar!")
                false
            }
        }
    }

    private fun setDefaultDateTime() {
        selectedDate = getCurrentDate()
        selectedTime = getCurrentTime()
        updateDateDisplay()
        updateTimeDisplay()
    }

    private fun updateDateDisplay() {
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvTanggal.text = displayFormat.format(calendar.time)

        val dataFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dataFormat.format(calendar.time)
    }

    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        selectedTime = timeFormat.format(calendar.time)
        tvWaktu.text = selectedTime
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date())
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun resetForm() {
        etNama.text.clear()
        npJumlahOrang.value = 2
        spMeja.setSelection(0)
        setDefaultDateTime()
        textInputLayoutNama.error = null
    }

    /**
     * Prefill form dengan data dari reservation (untuk edit)
     */
    private fun prefillForm(reservation: Reservation) {
        etNama.setText(reservation.nama)
        npJumlahOrang.value = reservation.jumlahOrang

        // Parse dan set tanggal
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(reservation.tanggal)
            date?.let {
                calendar.time = it
                updateDateDisplay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set meja di spinner
        val mejaPosition = listMeja.indexOf(reservation.meja)
        if (mejaPosition != -1) {
            spMeja.setSelection(mejaPosition)
        }
    }

    private fun validateNama(nama: String) {
        when (val result = InputValidator.validateNama(nama)) {
            is ValidationResult.Success -> {
                textInputLayoutNama.error = null
            }
            is ValidationResult.Error -> {
                textInputLayoutNama.error = result.message
            }
        }
    }

    private fun validateJumlahOrang(jumlah: Int) {
        when (val result = InputValidator.validateJumlahOrang(jumlah)) {
            is ValidationResult.Success -> {}
            is ValidationResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
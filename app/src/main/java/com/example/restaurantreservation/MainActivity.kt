package com.example.restaurantreservation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.DataTransferHelper
import com.example.restaurantreservation.utils.InputValidator
import com.example.restaurantreservation.utils.ReservationStorage
import com.example.restaurantreservation.utils.ValidationResult
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main Activity - Entry point aplikasi Reservasi Restoran
 *
 * Activity ini menangani:
 * - Input data reservasi dari user
 * - Validasi form input
 * - Navigasi ke DetailActivity dan ListActivity
 * - Pengelolaan state form
 *
 * @author Restaurant Reservation Team
 * @since 1.0
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etNama: EditText
    private lateinit var etCatatan: EditText
    private lateinit var npJumlahOrang: NumberPicker
    private lateinit var tvTanggal: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var spMeja: Spinner
    private lateinit var btnPilihTanggal: LinearLayout
    private lateinit var btnPilihWaktu: LinearLayout
    private lateinit var btnBuatReservasi: Button
    private lateinit var btnLihatDaftar: Button
    private lateinit var textInputLayoutNama: TextInputLayout

    // region - Data Variables
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedMeja: String = ""
    private val calendar = Calendar.getInstance()

    // Data untuk spinner meja
    private val listMeja = arrayOf(
        "Pilih Meja", "Meja 1", "Meja 2", "Meja 3", "Meja 4", "Meja 5",
        "Meja VIP 1", "Meja VIP 2", "Meja Keluarga 1", "Meja Keluarga 2"
    )
    // endregion

    // region - Lifecycle Methods

    /**
     * Dipanggil ketika activity pertama kali dibuat
     *
     * @param savedInstanceState State yang disimpan dari instance sebelumnya
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize storage
        ReservationStorage.init(this)

        initializeViews()
        setupComponents()
        setDefaultDateTime()
        handleIncomingIntent()

        printDebugInfo("MainActivity created successfully")
    }

    /**
     * Dipanggil ketika activity kembali ke foreground
     */
    override fun onResume() {
        super.onResume()
        printDebugInfo("MainActivity resumed")
    }
    // endregion

    // region - Initialization Methods

    /**
     * Initialize semua view components
     */
    private fun initializeViews() {
        etNama = findViewById(R.id.etNama)
        etCatatan = findViewById(R.id.etCatatan)
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

    /**
     * Setup semua komponen UI dan listeners
     */
    private fun setupComponents() {
        setupNumberPicker()
        setupSpinnerMeja()
        setupDatePicker()
        setupTimePicker()
        setupClickListeners()
        setupTextWatchers()
    }
    // endregion

    // region - Component Setup Methods

    /**
     * Setup NumberPicker untuk jumlah orang
     */
    private fun setupNumberPicker() {
        npJumlahOrang.apply {
            minValue = 1
            maxValue = 20
            value = 2 // Default value
            setFormatter { value -> "$value orang" }
        }
    }

    /**
     * Setup Spinner untuk pemilihan meja
     */
    private fun setupSpinnerMeja() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMeja)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMeja.adapter = adapter

        spMeja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedMeja = if (position > 0) listMeja[position] else ""
                printDebugInfo("Meja selected: $selectedMeja")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMeja = ""
            }
        }
    }

    /**
     * Setup DatePicker dialog untuk pemilihan tanggal
     */
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

    /**
     * Setup TimePicker dialog untuk pemilihan waktu
     */
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

    /**
     * Setup semua click listeners
     */
    private fun setupClickListeners() {
        btnBuatReservasi.setOnClickListener {
            buatReservasiDenganValidasi()
        }

        btnLihatDaftar.setOnClickListener {
            lihatDaftarReservasi()
        }
    }

    /**
     * Setup text watchers untuk real-time validation
     */
    private fun setupTextWatchers() {
        etNama.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                validateNama(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    // endregion

    // region - Business Logic Methods

    /**
     * Membuat reservasi baru dengan validasi lengkap
     */
    private fun buatReservasiDenganValidasi() {
        val nama = etNama.text.toString().trim()
        val jumlahOrang = npJumlahOrang.value
        val catatan = etCatatan.text.toString().trim()

        // Validasi form
        if (!isFormValid(nama)) return

        // Buat reservation object
        val reservation = Reservation.create(
            nama = nama,
            jumlahOrang = jumlahOrang,
            tanggal = selectedDate,
            waktu = selectedTime,
            meja = selectedMeja,
            catatan = catatan
        )

        // Validasi data sebelum dikirim
        if (!DataTransferHelper.validateReservationData(reservation)) {
            showError("Data reservasi tidak valid!")
            return
        }

        // Save reservation to storage
        ReservationStorage.addReservation(reservation)

        // Kirim data ke DetailActivity
        sendReservationData(reservation)

        // Reset form setelah success
        resetForm()

        printDebugInfo("Reservation created for: ${reservation.nama}")
    }

    /**
     * Validasi keseluruhan form
     *
     * @param nama Nama yang divalidasi
     * @return Boolean indicating valid atau tidak
     */
    private fun isFormValid(nama: String): Boolean {
        return when (InputValidator.validateNama(nama)) {
            is ValidationResult.Success -> true
            is ValidationResult.Error -> {
                showError("Harap isi form dengan benar!")
                false
            }
        }
    }

    /**
     * Kirim data reservasi ke DetailActivity
     *
     * @param reservation Data reservasi yang akan dikirim
     */
    private fun sendReservationData(reservation: Reservation) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_RESERVATION)
        showSuccessMessage("Reservasi berhasil dibuat untuk ${reservation.nama}!")
    }

    /**
     * Navigasi ke ListActivity
     */
    private fun lihatDaftarReservasi() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        printDebugInfo("Navigating to ListActivity")
    }
    // endregion

    // region - Helper Methods

    /**
     * Set default date and time values
     */
    private fun setDefaultDateTime() {
        selectedDate = getCurrentDate()
        selectedTime = getCurrentTime()
        updateDateDisplay()
        updateTimeDisplay()
    }

    /**
     * Update tampilan tanggal
     */
    private fun updateDateDisplay() {
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvTanggal.text = displayFormat.format(calendar.time)

        val dataFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dataFormat.format(calendar.time)
    }

    /**
     * Update tampilan waktu
     */
    private fun updateTimeDisplay() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        selectedTime = timeFormat.format(calendar.time)
        tvWaktu.text = selectedTime
    }

    /**
     * Dapatkan tanggal saat ini dalam format string
     *
     * @return String tanggal saat ini
     */
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Dapatkan waktu saat ini dalam format string
     *
     * @return String waktu saat ini
     */
    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date())
    }

    /**
     * Reset form ke state awal
     */
    private fun resetForm() {
        etNama.text?.clear()
        etCatatan.text?.clear()
        npJumlahOrang.value = 2
        spMeja.setSelection(0)
        setDefaultDateTime()
        textInputLayoutNama.error = null

        printDebugInfo("Form reset successfully")
    }

    /**
     * Validasi input nama secara real-time
     *
     * @param nama Nama yang divalidasi
     */
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
    // endregion

    // region - Intent Handling

    /**
     * Handle incoming intent dari activity lain
     */
    private fun handleIncomingIntent() {
        val reservation = DataTransferHelper.getReservationFromIntent(intent)
        reservation?.let {
            when (intent.action) {
                Constants.ACTION_EDIT -> prefillFormForEdit(it)
            }
        }
    }

    /**
     * Prefill form dengan data dari reservasi yang akan di-edit
     *
     * @param reservation Data reservasi untuk prefill
     */
    private fun prefillFormForEdit(reservation: Reservation) {
        etNama.setText(reservation.nama)
        npJumlahOrang.value = reservation.jumlahOrang
        etCatatan.setText(reservation.catatan)

        // Set tanggal
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.parse(reservation.tanggal)
            date?.let {
                calendar.time = it
                updateDateDisplay()
            }
        } catch (e: Exception) {
            printError("Error parsing date: ${e.message}")
        }

        // Set waktu
        try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val time = timeFormat.parse(reservation.waktu)
            time?.let {
                calendar.time = it
                updateTimeDisplay()
            }
        } catch (e: Exception) {
            printError("Error parsing time: ${e.message}")
        }

        // Set meja
        val mejaPosition = listMeja.indexOf(reservation.meja)
        if (mejaPosition != -1) {
            spMeja.setSelection(mejaPosition)
        }

        btnBuatReservasi.text = "Update Reservasi"

        printDebugInfo("Form prefilled for editing: ${reservation.nama}")
    }

    /**
     * Handle result dari activity lain
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_CODE_CREATE_RESERVATION -> handleCreateReservationResult(resultCode, data)
        }
    }

    /**
     * Handle result dari pembuatan reservasi
     */
    private fun handleCreateReservationResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Constants.RESULT_RESERVATION_CREATED -> {
                val reservation = DataTransferHelper.getReservationFromIntent(data!!)
                reservation?.let {
                    showSuccessMessage("Reservasi berhasil dibuat untuk ${it.nama}!")

                    // Navigate to ListActivity with new reservation data
                    val listIntent = Intent(this, ListActivity::class.java).apply {
                        putExtra(Constants.KEY_NEW_RESERVATION, reservation)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(listIntent)
                }
            }
            RESULT_CANCELED -> {
                showInfo("Reservasi dibatalkan")
            }
        }
    }
    // endregion

    // region - UI Feedback Methods

    /**
     * Tampilkan error message
     *
     * @param message Pesan error yang akan ditampilkan
     */
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        printError("UI Error: $message")
    }

    /**
     * Tampilkan success message
     *
     * @param message Pesan success yang akan ditampilkan
     */
    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        printDebugInfo("UI Success: $message")
    }

    /**
     * Tampilkan info message
     *
     * @param message Pesan info yang akan ditampilkan
     */
    private fun showInfo(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        printDebugInfo("UI Info: $message")
    }
    // endregion

    // region - Debugging & Logging

    /**
     * Print debug information
     *
     * @param message Pesan debug
     */
    private fun printDebugInfo(message: String) {
        if (BuildConfig.DEBUG) {
            println("DEBUG - MainActivity: $message")
        }
    }

    /**
     * Print error information
     *
     * @param message Pesan error
     */
    private fun printError(message: String) {
        println("ERROR - MainActivity: $message")
    }
    // endregion
}
package com.example.restaurantreservation

import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.DataTransferHelper
import com.example.restaurantreservation.utils.InputValidator
import com.example.restaurantreservation.utils.ValidationResult
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etNama: EditText
    private lateinit var etCatatan: EditText
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
    private val listMeja = arrayOf("Pilih Meja", "Meja 1", "Meja 2", "Meja 3", "Meja 4", "Meja 5", "Meja VIP 1", "Meja VIP 2", "Meja Keluarga 1", "Meja Keluarga 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupComponents()
        setDefaultDateTime()
        handleIncomingIntent()
    }

    private fun initViews() {
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

    private fun setupComponents() {
        setupNumberPicker()
        setupSpinnerMeja()
        setupDatePicker()
        setupTimePicker()
        setupClickListeners()
        setupTextWatchers()
    }

    private fun setupNumberPicker() {
        npJumlahOrang.minValue = 1
        npJumlahOrang.maxValue = 20
        npJumlahOrang.value = 2
        npJumlahOrang.setFormatter { value -> "$value orang" }
    }

    private fun setupSpinnerMeja() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMeja)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMeja.adapter = adapter

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
            DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
                val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
                datePicker.maxDate = maxDate.timeInMillis
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
            TimePickerDialog(this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
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

    // === DATA SENDING METHODS ===

    /**
     * METHOD 1: Mengirim data dengan multiple approaches
     */
    private fun buatReservasiDenganValidasi() {
        val nama = etNama.text.toString().trim()
        val jumlahOrang = npJumlahOrang.value
        val catatan = etCatatan.text.toString().trim()

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
            Toast.makeText(this, "Data reservasi tidak valid!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log data transfer untuk debugging
        DataTransferHelper.logDataTransfer("MainActivity", "DetailActivity", reservation)

        // Pilih salah satu method pengiriman data:
        when ((1..3).random()) { // Random pilih method untuk demonstrasi
            1 -> sendDataMethod1(reservation) // Individual fields
            2 -> sendDataMethod2(reservation) // Parcelable object
            3 -> sendDataMethod3(reservation) // Bundle
        }
    }

    /**
     * METHOD 1A: Mengirim data sebagai individual fields
     */
    private fun sendDataMethod1(reservation: Reservation) {
        println("Menggunakan METHOD 1: Individual Fields")

        val intent = Intent(this, DetailActivity::class.java).apply {
            // Kirim data sebagai individual fields
            putExtra(Constants.KEY_RESERVATION_ID, reservation.id)
            putExtra(Constants.KEY_NAMA, reservation.nama)
            putExtra(Constants.KEY_JUMLAH_ORANG, reservation.jumlahOrang)
            putExtra(Constants.KEY_TANGGAL, reservation.tanggal)
            putExtra(Constants.KEY_WAKTU, reservation.waktu)
            putExtra(Constants.KEY_MEJA, reservation.meja)
            putExtra(Constants.KEY_CATATAN, reservation.catatan)
            putExtra(Constants.KEY_STATUS, reservation.status)
            putExtra(Constants.KEY_CREATED_AT, reservation.createdAt)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_RESERVATION)
    }

    /**
     * METHOD 1B: Mengirim data sebagai Parcelable object
     */
    private fun sendDataMethod2(reservation: Reservation) {
        println("Menggunakan METHOD 2: Parcelable Object")

        val intent = Intent(this, DetailActivity::class.java).apply {
            // Kirim data sebagai Parcelable object (most efficient)
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_CREATE)
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_RESERVATION)
    }

    /**
     * METHOD 1C: Mengirim data menggunakan Bundle
     */
    private fun sendDataMethod3(reservation: Reservation) {
        println("Menggunakan METHOD 3: Bundle")

        val bundle = DataTransferHelper.createReservationBundle(reservation)
        bundle.putString(Constants.KEY_ACTION, Constants.ACTION_CREATE)

        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtras(bundle)
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_RESERVATION)
    }

    /**
     * METHOD 2: Mengirim data ke ListActivity
     */
    private fun lihatDaftarReservasi() {
        val intent = Intent(this, ListActivity::class.java).apply {
            // Bisa mengirim filter atau data lain ke ListActivity
            putExtra(Constants.KEY_ACTION, Constants.ACTION_VIEW)
            putExtra(Constants.BUNDLE_FILTER_STATUS, "all") // all, confirmed, pending
        }

        startActivity(intent)
    }

    /**
     * METHOD 3: Handle result dari Activity lain
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_CODE_CREATE_RESERVATION -> {
                when (resultCode) {
                    Constants.RESULT_RESERVATION_CREATED -> {
                        val reservation = DataTransferHelper.getReservationFromIntent(data!!)
                        reservation?.let {
                            showSuccessMessage("Reservasi berhasil dibuat untuk ${it.nama}!")
                            resetForm()
                        }
                    }
                    RESULT_CANCELED -> {
                        Toast.makeText(this, "Reservasi dibatalkan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * METHOD 4: Handle incoming intent dengan data
     */
    private fun handleIncomingIntent() {
        // Handle data yang dikirim ke MainActivity
        val action = intent.action
        val reservation = DataTransferHelper.getReservationFromIntent(intent)

        reservation?.let {
            when (action) {
                Constants.ACTION_EDIT -> {
                    prefillFormForEdit(it)
                }
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
        etCatatan.text.clear()
        npJumlahOrang.value = 2
        spMeja.setSelection(0)
        setDefaultDateTime()
        textInputLayoutNama.error = null
    }

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
            e.printStackTrace()
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
            e.printStackTrace()
        }

        // Set meja
        val mejaPosition = listMeja.indexOf(reservation.meja)
        if (mejaPosition != -1) {
            spMeja.setSelection(mejaPosition)
        }

        btnBuatReservasi.text = "Update Reservasi"
    }

    private fun validateNama(nama: String) {
        when (val result = InputValidator.validateNama(nama)) {
            is ValidationResult.Success -> textInputLayoutNama.error = null
            is ValidationResult.Error -> textInputLayoutNama.error = result.message
        }
    }
}
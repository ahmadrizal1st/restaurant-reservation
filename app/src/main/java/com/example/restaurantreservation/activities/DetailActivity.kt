package com.example.restaurantreservation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.DataReceiverHelper
import com.example.restaurantreservation.utils.IntentUtils
import com.example.restaurantreservation.utils.ValidationResult

class DetailActivity : AppCompatActivity() {

    private lateinit var tvNama: TextView
    private lateinit var tvJumlahOrang: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var tvMeja: TextView
    // private lateinit var tvCatatan: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvReservationId: TextView

    // private lateinit var labelCatatan: TextView
    // private lateinit var containerCatatan: LinearLayout

    private lateinit var btnBukaMaps: Button
    private lateinit var btnTeleponRestoran: Button
    private lateinit var btnBagikanReservasi: Button
    private lateinit var btnBukaWebsite: Button
    private lateinit var btnKirimEmail: Button
    // private lateinit var btnBukaKalender: Button
    // private lateinit var btnWhatsApp: Button
    private lateinit var btnEditReservasi: Button
    // private lateinit var btnKembali: Button
    private lateinit var toolbar: Toolbar

    private lateinit var reservation: Reservation
    private var action: String = Constants.ACTION_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initViews()
        terimaDanProsesData()
        tampilkanDataReservasi()
        setupUIberdasarkanAction()
        setupClickListeners()
    }

    private fun initViews() {
        // TextViews
        tvNama = findViewById(R.id.tvNama)
        tvJumlahOrang = findViewById(R.id.tvJumlahOrang)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvWaktu = findViewById(R.id.tvWaktu)
        tvMeja = findViewById(R.id.tvMeja)
        // tvCatatan = findViewById(R.id.tvCatatan)
        tvStatus = findViewById(R.id.tvStatus)
        tvCreatedAt = findViewById(R.id.tvCreatedAt)
        tvReservationId = findViewById(R.id.tvReservationId)

        // Layout components
        // labelCatatan = findViewById(R.id.labelCatatan)
        // containerCatatan = findViewById(R.id.containerCatatan)

        // Buttons
        btnBukaMaps = findViewById(R.id.btnBukaMaps)
        btnTeleponRestoran = findViewById(R.id.btnTeleponRestoran)
        btnBagikanReservasi = findViewById(R.id.btnBagikanReservasi)
        btnBukaWebsite = findViewById(R.id.btnBukaWebsite)
        btnKirimEmail = findViewById(R.id.btnKirimEmail)
        // btnBukaKalender = findViewById(R.id.btnBukaKalender)
        // btnWhatsApp = findViewById(R.id.btnWhatsApp)
        btnEditReservasi = findViewById(R.id.btnEditReservasi)
        // btnKembali = findViewById(R.id.btnKembali)

        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * METHOD 1: Menerima dan memproses data dari Intent
     */
    private fun terimaDanProsesData() {
        println("=== MENERIMA DATA DARI INTENT ===")

        // Cek apakah intent memiliki data
        if (!DataReceiverHelper.hasReservationData(intent)) {
            showError("Tidak ada data reservasi yang diterima")
            finish()
            return
        }

        // Dapatkan action dari intent
        action = DataReceiverHelper.getActionFromIntent(intent)
        println("Action: $action")

        // Dapatkan data reservasi dengan multiple fallback methods
        reservation = DataReceiverHelper.getReservationFromIntent(intent) ?: run {
            showError("Gagal memproses data reservasi")
            finish()
            return
        }

        // Validasi data yang diterima
        when (val validationResult = DataReceiverHelper.validateReceivedData(reservation)) {
            is ValidationResult.Success -> {
                println("Data reservasi valid: ${reservation.nama}")
            }
            is ValidationResult.Error -> {
                showError(validationResult.message)
                // Tetap lanjutkan, tapi dengan data yang mungkin tidak lengkap
            }
        }

        // Log additional parameters
        val additionalParams = DataReceiverHelper.getAdditionalParams(intent)
        println("Additional Params: $additionalParams")
    }

    /**
     * METHOD 2: Menampilkan data reservasi di UI
     */
    private fun tampilkanDataReservasi() {
        try {
            // Data utama
            tvNama.text = reservation.nama
            tvJumlahOrang.text = "${reservation.jumlahOrang} orang"
            tvTanggal.text = reservation.getFormattedDate()
            tvWaktu.text = reservation.getFormattedTime()
            tvMeja.text = reservation.meja
            tvStatus.text = reservation.status
            tvReservationId.text = "ID: ${reservation.id}"
            tvCreatedAt.text = "Dibuat: ${reservation.getCreatedAtFormatted()}"

            // Handle catatan (bisa kosong)
            // if (reservation.catatan.isNotBlank()) {
            //     tvCatatan.text = reservation.catatan
            //     containerCatatan.visibility = android.view.View.VISIBLE
            // } else {
            //     containerCatatan.visibility = android.view.View.GONE
            // }

            // Set warna status
            setStatusColor(reservation.status)

            // Set judul activity berdasarkan action
            setActivityTitle()

        } catch (e: Exception) {
            showError("Error menampilkan data: ${e.message}")
        }
    }

    /**
     * METHOD 3: Setup UI berdasarkan action
     */
    private fun setupUIberdasarkanAction() {
        when (action) {
            Constants.ACTION_CREATE -> {
                setTitle("Reservasi Baru")
                btnEditReservasi.visibility = android.view.View.GONE
            }
            Constants.ACTION_EDIT -> {
                setTitle("Edit Reservasi")
                btnEditReservasi.text = "Update Reservasi"
            }
            Constants.ACTION_VIEW -> {
                setTitle("Detail Reservasi")
                // Default view
            }
        }
    }

    private fun setupClickListeners() {
        // Implicit Intents
        btnBukaMaps.setOnClickListener { IntentUtils.openMaps(this) }
        btnTeleponRestoran.setOnClickListener { IntentUtils.callRestaurant(this) }
        btnBagikanReservasi.setOnClickListener { IntentUtils.shareReservation(this, reservation) }
        btnBukaWebsite.setOnClickListener { IntentUtils.openWebsite(this) }
        btnKirimEmail.setOnClickListener { IntentUtils.sendConfirmationEmail(this, reservation) }
        // btnBukaKalender.setOnClickListener { IntentUtils.addToCalendar(this, reservation) }
        // btnWhatsApp.setOnClickListener { IntentUtils.openWhatsApp(this, reservation) }

        // Edit Reservasi
        btnEditReservasi.setOnClickListener {
            editReservasi()
        }

        // Kembali dengan result
        // btnKembali.setOnClickListener {
        //     kembaliDenganResult()
        // }
    }

    /**
     * METHOD 4: Edit reservasi dan kirim data kembali
     */
    private fun editReservasi() {
        val resultIntent = Intent().apply {
            // Kirim data reservasi yang sudah di-update (dalam kasus real, mungkin ada form edit)
            val updatedReservation = reservation.copy(
                status = "Updated",
                updatedAt = System.currentTimeMillis()
            )
            putExtra(Constants.KEY_RESERVATION_DATA, updatedReservation)
        }

        setResult(Constants.RESULT_RESERVATION_UPDATED, resultIntent)
        showSuccess("Reservasi berhasil diupdate!")

        // Refresh tampilan
        reservation = reservation.copy(status = "Updated", updatedAt = System.currentTimeMillis())
        tampilkanDataReservasi()
    }

    /**
     * METHOD 5: Kembali dengan membawa result
     */
    private fun kembaliDenganResult() {
        when (action) {
            Constants.ACTION_CREATE -> {
                // Return result to MainActivity
                val resultIntent = Intent().apply {
                    putExtra(Constants.KEY_RESERVATION_DATA, reservation)
                }
                setResult(Constants.RESULT_RESERVATION_CREATED, resultIntent)
            }
            Constants.ACTION_EDIT -> {
                // Already handled in editReservasi()
            }
            else -> {
                setResult(RESULT_CANCELED)
            }
        }
        finish()
    }

    /**
     * METHOD 6: Handle konfigurasi perubahan
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Simpan data penting saat configuration change
        outState.putParcelable(Constants.KEY_RESERVATION_DATA, reservation)
        outState.putString(Constants.KEY_ACTION, action)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Pulihkan data saat configuration change
        savedInstanceState.getParcelable<Reservation>(Constants.KEY_RESERVATION_DATA)?.let {
            reservation = it
        }
        action = savedInstanceState.getString(Constants.KEY_ACTION) ?: Constants.ACTION_VIEW
        tampilkanDataReservasi()
    }

    // === HELPER METHODS ===

    private fun setStatusColor(status: String) {
        val color = when (status.toLowerCase()) {
            "confirmed", "confirmed" -> android.R.color.holo_green_dark
            "pending" -> android.R.color.holo_orange_dark
            "cancelled" -> android.R.color.holo_red_dark
            "updated" -> android.R.color.holo_blue_dark
            else -> android.R.color.black
        }
        tvStatus.setTextColor(getColor(color))
    }

    private fun setActivityTitle() {
        val title = when (action) {
            Constants.ACTION_CREATE -> "Reservasi Baru - ${reservation.nama}"
            Constants.ACTION_EDIT -> "Edit Reservasi - ${reservation.nama}"
            else -> "Detail Reservasi - ${reservation.nama}"
        }
        setTitle(title)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * METHOD 7: Handle back button press
     */
    override fun onBackPressed() {
        kembaliDenganResult()
    }

    /**
     * Handle toolbar navigation click
     */
    override fun onSupportNavigateUp(): Boolean {
        kembaliDenganResult()
        return true
    }
}
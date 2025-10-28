package com.example.restaurantreservation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreservation.adapter.ReservationAdapter
import com.example.restaurantreservation.adapter.SortField
import com.example.restaurantreservation.adapter.SortOrder
import com.example.restaurantreservation.interfaces.OnReservationClickListener
import com.example.restaurantreservation.model.Reservation
import com.example.restaurantreservation.utils.Constants
import com.example.restaurantreservation.utils.IntentUtils
import java.util.*

class ListActivity : AppCompatActivity(), OnReservationClickListener {

    // RecyclerView components
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationAdapter
    private lateinit var emptyState: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout

    // Filter and Search components
    private lateinit var searchView: SearchView
    private lateinit var spinnerFilter: Spinner

    // Data
    private val reservationList = mutableListOf<Reservation>()
    private var filteredList = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        initViews()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilterAndSearch()
        loadReservationData()
        setupClickListeners()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewReservations)
        emptyState = findViewById(R.id.tvEmptyState)
        progressBar = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Daftar Reservasi"
    }

    /**
     * METHOD 1: Setup RecyclerView dengan semua konfigurasi
     */
    private fun setupRecyclerView() {
        // Setup Adapter
        adapter = ReservationAdapter(this)

        // Setup LayoutManager dengan konfigurasi optimal
        val layoutManager = LinearLayoutManager(this)

        // Setup RecyclerView
        recyclerView.apply {
            setHasFixedSize(true) // Improve performance jika item height fixed
            this.layoutManager = layoutManager
            adapter = this@ListActivity.adapter

            // Add item decoration untuk spacing
            addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(
                this@ListActivity,
                LinearLayoutManager.VERTICAL
            ))

            // Add scroll listener untuk load more (jika diperlukan)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // Bisa implement infinite scrolling di sini
                }
            })

            // Enable predictive animations untuk smooth scrolling
            itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator().apply {
                // Optimize animations
                supportsChangeAnimations = true
            }
        }

        // Show empty state jika tidak ada data
        updateEmptyState()
    }

    /**
     * METHOD 2: Setup Swipe to Refresh
     */
    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        swipeRefreshLayout.setOnRefreshListener {
            // Simulate data refresh
            loadReservationData()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * METHOD 3: Setup Filter dan Search functionality
     */
    private fun setupFilterAndSearch() {
        // Filter spinner akan di-setup di onCreateOptionsMenu
    }

    /**
     * METHOD 4: Load sample data untuk demonstrasi
     */
    private fun loadReservationData() {
        showLoading()

        // Simulate network delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            generateSampleData()
            hideLoading()
            updateEmptyState()
        }, 1000)
    }

    /**
     * METHOD 5: Generate sample data untuk testing
     */
    private fun generateSampleData() {
        reservationList.clear()

        // Add sample reservations
        reservationList.addAll(listOf(
            Reservation.create(
                nama = "Ahmad Wijaya",
                jumlahOrang = 4,
                tanggal = "15/12/2024",
                waktu = "19:00",
                meja = "Meja VIP 1",
                catatan = "Acara ulang tahun",
                status = "Confirmed"
            ),
            Reservation.create(
                nama = "Siti Rahayu",
                jumlahOrang = 2,
                tanggal = "16/12/2024",
                waktu = "20:30",
                meja = "Meja 3",
                catatan = "Malam romantis",
                status = "Confirmed"
            ),
            Reservation.create(
                nama = "Budi Santoso",
                jumlahOrang = 6,
                tanggal = "17/12/2024",
                waktu = "18:00",
                meja = "Meja Keluarga 1",
                catatan = "Kumpul keluarga",
                status = "Pending"
            ),
            Reservation.create(
                nama = "Maria Magdalena",
                jumlahOrang = 3,
                tanggal = "15/12/2024",
                waktu = "21:00",
                meja = "Meja 2",
                catatan = "Meeting bisnis",
                status = "Confirmed"
            ),
            Reservation.create(
                nama = "Rizki Pratama",
                jumlahOrang = 5,
                tanggal = "18/12/2024",
                waktu = "19:30",
                meja = "Meja VIP 2",
                catatan = "Acara kantor",
                status = "Pending"
            ),
            Reservation.create(
                nama = "Dewi Lestari",
                jumlahOrang = 2,
                tanggal = "16/12/2024",
                waktu = "20:00",
                meja = "Meja 1",
                catatan = "Date anniversary",
                status = "Cancelled"
            ),
            Reservation.create(
                nama = "Joko Widodo",
                jumlahOrang = 8,
                tanggal = "19/12/2024",
                waktu = "18:30",
                meja = "Meja Keluarga 2",
                catatan = "Acara besar",
                status = "Confirmed"
            ),
            Reservation.create(
                nama = "Ani Susanti",
                jumlahOrang = 4,
                tanggal = "17/12/2024",
                waktu = "19:00",
                meja = "Meja 4",
                catatan = "Makan malam teman",
                status = "Pending"
            )
        ))

        // Submit data ke adapter
        adapter.submitList(reservationList)
        filteredList.clear()
        filteredList.addAll(reservationList)

        // Show data statistics
        showDataStatistics()
    }

    /**
     * METHOD 6: Update empty state visibility
     */
    private fun updateEmptyState() {
        if (adapter.itemCount == 0) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            emptyState.text = getString(R.string.empty_reservation_list)
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        }
    }

    /**
     * METHOD 7: Show loading state
     */
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    /**
     * METHOD 8: Hide loading state
     */
    private fun hideLoading() {
        progressBar.visibility = View.GONE
        updateEmptyState()
    }

    /**
     * METHOD 9: Show data statistics
     */
    private fun showDataStatistics() {
        val totalReservations = adapter.itemCount
        val totalPeople = adapter.getTotalPeople()
        val confirmedCount = adapter.getReservationsByStatus("confirmed").size

        // Bisa ditampilkan di subtitle toolbar atau snackbar
        supportActionBar?.subtitle = "$totalReservations reservasi • $totalPeople orang • $confirmedCount dikonfirmasi"
    }

    // === ON RESERVATION CLICK LISTENER METHODS ===

    override fun onReservationClick(reservation: Reservation, position: Int) {
        // Navigate ke DetailActivity
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_VIEW)
        }
        startActivity(intent)

        // Add animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onEditClick(reservation: Reservation, position: Int) {
        // Navigate ke DetailActivity untuk edit
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(Constants.KEY_RESERVATION_DATA, reservation)
            putExtra(Constants.KEY_ACTION, Constants.ACTION_EDIT)
        }
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_RESERVATION)

        // Show edit message
        Toast.makeText(this, "Edit reservasi ${reservation.nama}", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteClick(reservation: Reservation, position: Int) {
        // Show confirmation dialog
        android.app.AlertDialog.Builder(this)
            .setTitle("Hapus Reservasi")
            .setMessage("Apakah Anda yakin ingin menghapus reservasi ${reservation.nama}?")
            .setPositiveButton("Hapus") { dialog, which ->
                // Remove from adapter
                adapter.removeItem(position)
                updateEmptyState()
                showDataStatistics()

                // Show confirmation
                Toast.makeText(this, "Reservasi berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onShareClick(reservation: Reservation, position: Int) {
        // Share reservation details
        IntentUtils.shareReservation(this, reservation)
    }

    override fun onReservationLongClick(reservation: Reservation, position: Int): Boolean {
        // Show quick actions menu
        showQuickActionsMenu(reservation, position)
        return true
    }

    /**
     * METHOD 10: Show quick actions menu pada long press
     */
    private fun showQuickActionsMenu(reservation: Reservation, position: Int) {
        val options = arrayOf("Lihat Detail", "Edit", "Share", "Hapus")

        android.app.AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> onReservationClick(reservation, position)
                    1 -> onEditClick(reservation, position)
                    2 -> onShareClick(reservation, position)
                    3 -> onDeleteClick(reservation, position)
                }
            }
            .show()
    }

    // === MENU AND FILTER METHODS ===

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_reservation_list, menu)

        // Setup search view
        val searchItem = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as SearchView
        setupSearchView()

        // Setup filter spinner
        setupFilterSpinner(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_add -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                loadReservationData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * METHOD 11: Setup search view functionality
     */
    private fun setupSearchView() {
        searchView.apply {
            queryHint = "Cari nama, meja, atau status..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    filterReservations(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filterReservations(newText)
                    return true
                }
            })

            // Clear search when close
            setOnCloseListener {
                filterReservations("")
                false
            }
        }
    }

    /**
     * METHOD 12: Setup filter spinner
     */
    private fun setupFilterSpinner(menu: Menu?) {
        val filterItem = menu?.findItem(R.id.action_filter)
        val filterSpinner = filterItem?.actionView as Spinner

        val filterOptions = arrayOf("Semua Status", "Confirmed", "Pending", "Cancelled")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = spinnerAdapter

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                0 -> this@ListActivity.adapter.submitList(reservationList.toMutableList()) // All
                1 -> this@ListActivity.adapter.submitList(reservationList.filter { it.status.equals("confirmed", true) }.toMutableList())
                2 -> this@ListActivity.adapter.submitList(reservationList.filter { it.status.equals("pending", true) }.toMutableList())
                3 -> this@ListActivity.adapter.submitList(reservationList.filter { it.status.equals("cancelled", true) }.toMutableList())
                }
                updateEmptyState()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    /**
     * METHOD 13: Filter reservations berdasarkan query
     */
    private fun filterReservations(query: String) {
        adapter.filter(query, reservationList)
        updateEmptyState()
    }

    /**
     * METHOD 14: Show sort dialog
     */
    private fun showSortDialog() {
        val sortOptions = arrayOf("Nama (A-Z)", "Nama (Z-A)", "Tanggal (Terbaru)", "Tanggal (Terlama)", "Jumlah Orang")

        android.app.AlertDialog.Builder(this)
            .setTitle("Urutkan Berdasarkan")
            .setItems(sortOptions) { dialog, which ->
                when (which) {
                    0 -> adapter.sort(SortField.NAME, SortOrder.ASCENDING)
                    1 -> adapter.sort(SortField.NAME, SortOrder.DESCENDING)
                    2 -> adapter.sort(SortField.DATE, SortOrder.DESCENDING)
                    3 -> adapter.sort(SortField.DATE, SortOrder.ASCENDING)
                    4 -> adapter.sort(SortField.PEOPLE, SortOrder.DESCENDING)
                }
                Toast.makeText(this, "Data diurutkan", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    // === ACTIVITY RESULT HANDLING ===

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_CODE_EDIT_RESERVATION -> {
                if (resultCode == Constants.RESULT_RESERVATION_UPDATED) {
                    val updatedReservation = data?.getParcelableExtra<Reservation>(Constants.KEY_RESERVATION_DATA)
                    updatedReservation?.let {
                        // Find and update the reservation in the list
                        val position = reservationList.indexOfFirst { reservation -> reservation.id == it.id }
                        if (position != -1) {
                            reservationList[position] = it
                            adapter.submitList(reservationList)
                            Toast.makeText(this, "Reservasi berhasil diupdate", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Setup any additional click listeners
        findViewById<Button>(R.id.btnAddReservation).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /**
     * METHOD 15: Handle configuration changes
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save important state if needed
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore state if needed
    }
}
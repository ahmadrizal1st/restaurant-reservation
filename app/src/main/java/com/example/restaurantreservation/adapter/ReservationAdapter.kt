package com.example.restaurantreservation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.restaurantreservation.R
import com.example.restaurantreservation.interfaces.OnReservationClickListener
import com.example.restaurantreservation.model.Reservation

/**
 * Adapter class untuk RecyclerView yang menampilkan list reservasi
 * Menggunakan ListAdapter dengan DiffUtil untuk performa optimal
 */
class ReservationAdapter(
    private val listener: OnReservationClickListener,
) : ListAdapter<Reservation, ReservationViewHolder>(ReservationDiffCallback()) {
    /**
     * Method untuk membuat ViewHolder baru
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReservationViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view, listener)
    }

    /**
     * Method untuk bind data ke ViewHolder
     */
    override fun onBindViewHolder(
        holder: ReservationViewHolder,
        position: Int,
    ) {
        val reservation = getItem(position)
        holder.bind(reservation, position)

        // Animate item ketika pertama kali di-bind
        if (position > 0) {
            holder.animateItem()
        }
    }

    /**
     * Method untuk bind data dengan payload (partial update)
     */
    override fun onBindViewHolder(
        holder: ReservationViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isNotEmpty()) {
            // Handle partial updates jika diperlukan
            when (payloads[0]) {
                is StatusPayload -> {
                    // Update hanya status
                    val reservation = getItem(position)
                    holder.bind(reservation, position)
                }
                else -> {
                    // Full bind untuk payload lainnya
                    super.onBindViewHolder(holder, position, payloads)
                }
            }
        } else {
            // Full bind tanpa payload
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    /**
     * Method untuk filter list berdasarkan query
     */
    fun filter(
        query: String,
        originalList: List<Reservation>,
    ) {
        val filteredList =
            if (query.isBlank()) {
                originalList
            } else {
                originalList.filter { reservation ->
                    reservation.nama.contains(query, true) ||
                        reservation.meja.contains(query, true) ||
                        reservation.status.contains(query, true) ||
                        reservation.tanggal.contains(query, true)
                }
            }
        submitList(filteredList)
    }

    /**
     * Method untuk sort list berdasarkan field tertentu
     */
    fun sort(
        sortBy: SortField,
        sortOrder: SortOrder,
    ) {
        val sortedList =
            currentList.sortedWith { r1, r2 ->
                when (sortBy) {
                    SortField.NAME -> compareValues(r1.nama, r2.nama)
                    SortField.DATE -> compareValues(r1.tanggal, r2.tanggal)
                    SortField.TIME -> compareValues(r1.waktu, r2.waktu)
                    SortField.STATUS -> compareValues(r1.status, r2.status)
                    SortField.PEOPLE -> compareValues(r1.jumlahOrang, r2.jumlahOrang)
                }.let { result ->
                    if (sortOrder == SortOrder.DESCENDING) -result else result
                }
            }
        submitList(sortedList)
    }

    /**
     * Method untuk mendapatkan total jumlah orang dari semua reservasi
     */
    fun getTotalPeople(): Int {
        return currentList.sumOf { it.jumlahOrang }
    }

    /**
     * Method untuk mendapatkan reservasi by status
     */
    fun getReservationsByStatus(status: String): List<Reservation> {
        return currentList.filter { it.status.equals(status, true) }
    }
}

/**
 * DiffUtil Callback untuk menghitung perbedaan antara old list dan new list
 */
class ReservationDiffCallback : DiffUtil.ItemCallback<Reservation>() {
    /**
     * Method untuk cek apakah items sama (berdasarkan unique identifier)
     */
    override fun areItemsTheSame(
        oldItem: Reservation,
        newItem: Reservation,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Method untuk cek apakah contents sama (data sama)
     */
    override fun areContentsTheSame(
        oldItem: Reservation,
        newItem: Reservation,
    ): Boolean {
        return oldItem == newItem
    }

    /**
     * Method untuk get change payload (optimization untuk partial update)
     */
    override fun getChangePayload(
        oldItem: Reservation,
        newItem: Reservation,
    ): Any? {
        return when {
            oldItem.status != newItem.status -> StatusPayload
            else -> null
        }
    }
}

/**
 * Payload untuk partial update
 */
object StatusPayload

/**
 * Enum untuk field sorting
 */
enum class SortField {
    NAME,
    DATE,
    TIME,
    STATUS,
    PEOPLE,
}

/**
 * Enum untuk order sorting
 */
enum class SortOrder {
    ASCENDING,
    DESCENDING,
}

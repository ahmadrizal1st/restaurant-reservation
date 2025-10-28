package com.example.restaurantreservation.adapter

import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreservation.R
import com.example.restaurantreservation.model.Reservation

/**
 * ViewHolder class untuk item reservasi di RecyclerView
 * Bertanggung jawab untuk menampilkan data dan menangani view components
 */
class ReservationViewHolder(
    itemView: View,
    private val listener: com.example.restaurantreservation.interfaces.OnReservationClickListener
) : RecyclerView.ViewHolder(itemView) {

    // Deklarasi semua view components
    private val tvNama: TextView = itemView.findViewById(R.id.tvItemNama)
    private val tvJumlahOrang: TextView = itemView.findViewById(R.id.tvItemJumlahOrang)
    private val tvTanggal: TextView = itemView.findViewById(R.id.tvItemTanggal)
    private val tvWaktu: TextView = itemView.findViewById(R.id.tvItemWaktu)
    private val tvMeja: TextView = itemView.findViewById(R.id.tvItemMeja)
    private val tvStatus: TextView = itemView.findViewById(R.id.tvItemStatus)
    private val tvReservationId: TextView = itemView.findViewById(R.id.tvItemReservationId)

    private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
    private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    private val btnShare: ImageButton = itemView.findViewById(R.id.btnShare)

    private val layoutStatus: LinearLayout = itemView.findViewById(R.id.layoutStatus)
    private val cardView: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cardReservation)

    /**
     * Method untuk bind data reservasi ke view
     * @param reservation Data reservasi yang akan ditampilkan
     * @param position Posisi item dalam list
     */
    fun bind(reservation: Reservation, position: Int) {
        // Set data ke view components
        setDataToViews(reservation)

        // Setup click listeners
        setupClickListeners(reservation, position)

        // Setup long click listener
        setupLongClickListener(reservation, position)

        // Apply styling berdasarkan status
        applyStatusStyling(reservation.status)

        // Apply conditional styling
        applyConditionalStyling(reservation, position)
    }

    /**
     * Method untuk men-set data reservasi ke view components
     */
    private fun setDataToViews(reservation: Reservation) {
        tvNama.text = reservation.nama
        tvJumlahOrang.text = formatJumlahOrang(reservation.jumlahOrang)
        tvTanggal.text = reservation.getFormattedDate()
        tvWaktu.text = reservation.getFormattedTime()
        tvMeja.text = reservation.meja
        tvStatus.text = formatStatus(reservation.status)
        tvReservationId.text = formatReservationId(reservation.id)
    }

    /**
     * Method untuk setup click listeners
     */
    private fun setupClickListeners(reservation: Reservation, position: Int) {
        // Click pada entire item
        itemView.setOnClickListener {
            listener.onReservationClick(reservation, position)
        }

        // Click pada tombol edit
        btnEdit.setOnClickListener {
            listener.onEditClick(reservation, position)
        }

        // Click pada tombol delete
        btnDelete.setOnClickListener {
            listener.onDeleteClick(reservation, position)
        }

        // Click pada tombol share
        btnShare.setOnClickListener {
            listener.onShareClick(reservation, position)
        }
    }

    /**
     * Method untuk setup long click listener
     */
    private fun setupLongClickListener(reservation: Reservation, position: Int) {
        itemView.setOnLongClickListener {
            listener.onReservationLongClick(reservation, position)
        }
    }

    /**
     * Method untuk apply styling berdasarkan status reservasi
     */
    private fun applyStatusStyling(status: String) {
        val (backgroundColor, textColor) = getStatusColors(status)

        layoutStatus.setBackgroundColor(
            ContextCompat.getColor(itemView.context, backgroundColor)
        )
        tvStatus.setTextColor(
            ContextCompat.getColor(itemView.context, textColor)
        )
    }

    /**
     * Method untuk apply conditional styling
     */
    private fun applyConditionalStyling(reservation: Reservation, position: Int) {
        // Highlight item berdasarkan posisi (ganjil/genap)
        if (position % 2 == 0) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.item_background_even)
            )
        } else {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, R.color.item_background_odd)
            )
        }

        // Tampilkan reservation ID hanya untuk debug
        tvReservationId.visibility = if (isInDebugMode()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * Method untuk mendapatkan warna berdasarkan status
     */
    private fun getStatusColors(status: String): Pair<Int, Int> {
        return when (status.toLowerCase()) {
            "confirmed", "confirmed" -> Pair(R.color.status_confirmed_bg, R.color.status_confirmed_text)
            "pending" -> Pair(R.color.status_pending_bg, R.color.status_pending_text)
            "cancelled" -> Pair(R.color.status_cancelled_bg, R.color.status_cancelled_text)
            "completed" -> Pair(R.color.status_completed_bg, R.color.status_completed_text)
            else -> Pair(R.color.status_default_bg, R.color.status_default_text)
        }
    }

    /**
     * Method untuk format jumlah orang
     */
    private fun formatJumlahOrang(jumlah: Int): String {
        return "$jumlah " + itemView.context.getString(
            if (jumlah == 1) R.string.person_singular else R.string.person_plural
        )
    }

    /**
     * Method untuk format status
     */
    private fun formatStatus(status: String): String {
        return when (status.toLowerCase()) {
            "confirmed" -> itemView.context.getString(R.string.status_confirmed)
            "pending" -> itemView.context.getString(R.string.status_pending)
            "cancelled" -> itemView.context.getString(R.string.status_cancelled)
            "completed" -> itemView.context.getString(R.string.status_completed)
            else -> status
        }
    }

    /**
     * Method untuk format reservation ID
     */
    private fun formatReservationId(id: String): String {
        return "ID: ${id.takeLast(6)}" // Show last 6 characters for brevity
    }

    /**
     * Method untuk cek apakah dalam mode debug
     */
    private fun isInDebugMode(): Boolean {
        return false // Dalam production, bisa di-set berdasarkan BuildConfig.DEBUG
    }

    /**
     * Method untuk animate item ketika data di-update
     */
    fun animateItem() {
        itemView.alpha = 0f
        itemView.animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(null)
    }

    /**
     * Method untuk animate item removal
     */
    fun animateRemoval() {
        itemView.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                itemView.visibility = View.GONE
            }
    }

    /**
     * Method untuk reset animation
     */
    fun resetAnimation() {
        itemView.alpha = 1f
        itemView.visibility = View.VISIBLE
    }
}
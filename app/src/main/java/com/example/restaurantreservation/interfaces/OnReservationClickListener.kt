package com.example.restaurantreservation.interfaces

import com.example.restaurantreservation.model.Reservation

/**
 * Interface untuk menangani click events pada item RecyclerView
 */
interface OnReservationClickListener {

    /**
     * Dipanggil ketika item reservasi di-click
     * @param reservation Data reservasi yang di-click
     * @param position Posisi item dalam list
     */
    fun onReservationClick(reservation: Reservation, position: Int)

    /**
     * Dipanggil ketika tombol edit pada item di-click
     * @param reservation Data reservasi yang akan di-edit
     * @param position Posisi item dalam list
     */
    fun onEditClick(reservation: Reservation, position: Int)

    /**
     * Dipanggil ketika tombol delete pada item di-click
     * @param reservation Data reservasi yang akan di-delete
     * @param position Posisi item dalam list
     */
    fun onDeleteClick(reservation: Reservation, position: Int)

    /**
     * Dipanggil ketika tombol share pada item di-click
     * @param reservation Data reservasi yang akan di-share
     * @param position Posisi item dalam list
     */
    fun onShareClick(reservation: Reservation, position: Int)

    /**
     * Dipanggil ketika item di-long-press
     * @param reservation Data reservasi yang di-long-press
     * @param position Posisi item dalam list
     */
    fun onReservationLongClick(reservation: Reservation, position: Int): Boolean
}
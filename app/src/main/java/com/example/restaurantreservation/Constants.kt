package com.example.restaurantreservation.utils

/**
 * Object untuk menyimpan constants yang digunakan di seluruh aplikasi
 * Terutama untuk key Intent extras
 */
object Constants {

    // Keys untuk Intent extras
    const val KEY_RESERVATION_DATA = "reservation_data"
    const val KEY_RESERVATION_ID = "reservation_id"
    const val KEY_NAMA = "nama"
    const val KEY_JUMLAH_ORANG = "jumlah_orang"
    const val KEY_TANGGAL = "tanggal"
    const val KEY_WAKTU = "waktu"
    const val KEY_MEJA = "meja"
    const val KEY_ACTION = "action"

    // Values untuk actions
    const val ACTION_CREATE = "create"
    const val ACTION_EDIT = "edit"
    const val ACTION_VIEW = "view"

    // Request codes untuk startActivityForResult
    const val REQUEST_CODE_CREATE_RESERVATION = 1001
    const val REQUEST_CODE_EDIT_RESERVATION = 1002
    const val REQUEST_CODE_VIEW_RESERVATION = 1003
}
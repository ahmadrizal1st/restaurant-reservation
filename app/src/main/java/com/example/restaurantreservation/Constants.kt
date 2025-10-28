package com.example.restaurantreservation.utils

/**
 * Object untuk menyimpan constants yang digunakan di seluruh aplikasi
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
    const val KEY_CATATAN = "catatan"
    const val KEY_STATUS = "status"
    const val KEY_CREATED_AT = "created_at"
    const val KEY_UPDATED_AT = "updated_at"
    const val KEY_ACTION = "action"

    // Values untuk actions
    const val ACTION_CREATE = "create"
    const val ACTION_EDIT = "edit"
    const val ACTION_VIEW = "view"
    const val ACTION_DELETE = "delete"

    // Request codes untuk startActivityForResult
    const val REQUEST_CODE_CREATE_RESERVATION = 1001
    const val REQUEST_CODE_EDIT_RESERVATION = 1002
    const val REQUEST_CODE_VIEW_RESERVATION = 1003
    const val REQUEST_CODE_DELETE_RESERVATION = 1004

    // Result codes
    const val RESULT_RESERVATION_CREATED = 2001
    const val RESULT_RESERVATION_UPDATED = 2002
    const val RESULT_RESERVATION_DELETED = 2003
    const val RESULT_RESERVATION_CANCELLED = 2004

    // Bundle keys
    const val BUNDLE_RESERVATION_LIST = "reservation_list"
    const val BUNDLE_SELECTED_RESERVATION = "selected_reservation"
    const val BUNDLE_FILTER_STATUS = "filter_status"
}
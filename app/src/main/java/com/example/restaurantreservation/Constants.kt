package com.example.restaurantreservation.utils

/**
 * Constants object untuk menyimpan semua constants yang digunakan dalam aplikasi
 *
 * Constants dikelompokkan berdasarkan fungsinya:
 * - Intent Keys: Untuk data transfer antar activity
 * - Action Types: Untuk menentukan jenis action
 * - Request Codes: Untuk startActivityForResult
 * - Result Codes: Untuk mengembalikan result
 * - Bundle Keys: Untuk data dalam Bundle
 * - Configuration: Konfigurasi aplikasi
 *
 * @author Restaurant Reservation Team
 * @since 1.0
 */
object Constants {

    // region - Intent Keys
    /** Key untuk data reservasi dalam bentuk Parcelable */
    const val KEY_RESERVATION_DATA = "reservation_data"

    /** Key untuk ID reservasi */
    const val KEY_RESERVATION_ID = "reservation_id"

    /** Key untuk nama pemesan */
    const val KEY_NAMA = "nama"

    /** Key untuk jumlah orang */
    const val KEY_JUMLAH_ORANG = "jumlah_orang"

    /** Key untuk tanggal reservasi */
    const val KEY_TANGGAL = "tanggal"

    /** Key untuk waktu reservasi */
    const val KEY_WAKTU = "waktu"

    /** Key untuk meja reservasi */
    const val KEY_MEJA = "meja"

    /** Key untuk catatan reservasi */
    const val KEY_CATATAN = "catatan"

    /** Key untuk status reservasi */
    const val KEY_STATUS = "status"

    /** Key untuk timestamp created */
    const val KEY_CREATED_AT = "created_at"

    /** Key untuk timestamp updated */
    const val KEY_UPDATED_AT = "updated_at"

    /** Key untuk action type */
    const val KEY_ACTION = "action"
    // endregion

    // region - Action Types
    /** Action untuk membuat reservasi baru */
    const val ACTION_CREATE = "create"

    /** Action untuk mengedit reservasi yang sudah ada */
    const val ACTION_EDIT = "edit"

    /** Action untuk melihat detail reservasi */
    const val ACTION_VIEW = "view"

    /** Action untuk menghapus reservasi */
    const val ACTION_DELETE = "delete"
    // endregion

    // region - Request Codes (for startActivityForResult)
    /** Request code untuk membuat reservasi */
    const val REQUEST_CODE_CREATE_RESERVATION = 1001

    /** Request code untuk mengedit reservasi */
    const val REQUEST_CODE_EDIT_RESERVATION = 1002

    /** Request code untuk melihat reservasi */
    const val REQUEST_CODE_VIEW_RESERVATION = 1003

    /** Request code untuk menghapus reservasi */
    const val REQUEST_CODE_DELETE_RESERVATION = 1004
    // endregion

    // region - Result Codes
    /** Result code untuk reservasi berhasil dibuat */
    const val RESULT_RESERVATION_CREATED = 2001

    /** Result code untuk reservasi berhasil diupdate */
    const val RESULT_RESERVATION_UPDATED = 2002

    /** Result code untuk reservasi berhasil dihapus */
    const val RESULT_RESERVATION_DELETED = 2003

    /** Result code untuk reservasi dibatalkan */
    const val RESULT_RESERVATION_CANCELLED = 2004
    // endregion

    // region - Bundle Keys
    /** Key untuk list reservasi dalam Bundle */
    const val BUNDLE_RESERVATION_LIST = "reservation_list"

    /** Key untuk selected reservation dalam Bundle */
    const val BUNDLE_SELECTED_RESERVATION = "selected_reservation"

    /** Key untuk filter status dalam Bundle */
    const val BUNDLE_FILTER_STATUS = "filter_status"

    /** Key untuk search query dalam Bundle */
    const val BUNDLE_SEARCH_QUERY = "search_query"
    // endregion

    // region - Configuration
    /** Default timeout untuk network requests (dalam milliseconds) */
    const val DEFAULT_TIMEOUT_MS = 30000L

    /** Maximum characters untuk nama pemesan */
    const val MAX_NAMA_LENGTH = 50

    /** Minimum characters untuk nama pemesan */
    const val MIN_NAMA_LENGTH = 3

    /** Maximum jumlah orang untuk reservasi */
    const val MAX_JUMLAH_ORANG = 50

    /** Minimum jumlah orang untuk reservasi */
    const val MIN_JUMLAH_ORANG = 1

    /** Date format yang digunakan dalam aplikasi */
    const val DATE_FORMAT = "dd/MM/yyyy"

    /** Time format yang digunakan dalam aplikasi */
    const val TIME_FORMAT = "HH:mm"

    /** Display date format untuk UI */
    const val DISPLAY_DATE_FORMAT = "EEEE, dd MMMM yyyy"

    /** Display time format untuk UI */
    const val DISPLAY_TIME_FORMAT = "HH:mm"
    // endregion

    // region - Validation Messages
    /** Pesan error untuk nama kosong */
    const val ERROR_NAMA_KOSONG = "Nama harus diisi!"

    /** Pesan error untuk nama terlalu pendek */
    const val ERROR_NAMA_PENDEK = "Nama minimal 3 karakter!"

    /** Pesan error untuk nama terlalu panjang */
    const val ERROR_NAMA_PANJANG = "Nama maksimal 50 karakter!"

    /** Pesan error untuk format nama tidak valid */
    const val ERROR_NAMA_FORMAT = "Nama hanya boleh mengandung huruf dan spasi!"

    /** Pesan error untuk jumlah orang tidak valid */
    const val ERROR_JUMLAH_ORANG = "Jumlah orang harus antara 1-50!"

    /** Pesan error untuk tanggal tidak valid */
    const val ERROR_TANGGAL = "Tanggal tidak valid!"
    // endregion
}
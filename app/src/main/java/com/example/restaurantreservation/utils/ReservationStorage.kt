package com.example.restaurantreservation.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.restaurantreservation.model.Reservation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Utility class for persisting reservations using SharedPreferences
 */
object ReservationStorage {
    private const val PREFS_NAME = "restaurant_reservation_prefs"
    private const val KEY_RESERVATIONS = "reservations"

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    /**
     * Initialize the storage with context
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save list of reservations to SharedPreferences
     */
    fun saveReservations(reservations: List<Reservation>) {
        val json = gson.toJson(reservations)
        sharedPreferences.edit().putString(KEY_RESERVATIONS, json).apply()
    }

    /**
     * Load list of reservations from SharedPreferences
     */
    fun loadReservations(): MutableList<Reservation> {
        val json = sharedPreferences.getString(KEY_RESERVATIONS, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Reservation>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }
    }

    /**
     * Add a single reservation and save
     */
    fun addReservation(reservation: Reservation) {
        val reservations = loadReservations()
        reservations.add(reservation)
        saveReservations(reservations)
    }

    /**
     * Update a reservation by ID and save
     */
    fun updateReservation(updatedReservation: Reservation) {
        val reservations = loadReservations()
        val index = reservations.indexOfFirst { it.id == updatedReservation.id }
        if (index != -1) {
            reservations[index] = updatedReservation
            saveReservations(reservations)
        }
    }

    /**
     * Remove a reservation by ID and save
     */
    fun removeReservation(reservationId: String) {
        val reservations = loadReservations()
        reservations.removeAll { it.id == reservationId }
        saveReservations(reservations)
    }

    /**
     * Clear all reservations
     */
    fun clearAllReservations() {
        sharedPreferences.edit().remove(KEY_RESERVATIONS).apply()
    }
}

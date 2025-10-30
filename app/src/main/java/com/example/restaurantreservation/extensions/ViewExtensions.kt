package com.example.restaurantreservation.extensions

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Extension functions untuk View components
 *
 * Berisi utility functions untuk mempermudah operasi pada View
 *
 * @author Restaurant Reservation Team
 * @since 1.0
 */

/**
 * Extension function untuk menampilkan View
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Extension function untuk menyembunyikan View
 */
fun View.hide() {
    this.visibility = View.GONE
}

/**
 * Extension function untuk membuat View transparan
 */
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 * Extension function untuk toggle visibility View
 */
fun View.toggleVisibility() {
    this.visibility = if (this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

/**
 * Extension function untuk menampilkan Toast pendek
 */
fun View.showShortToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

/**
 * Extension function untuk menampilkan Toast panjang
 */
fun View.showLongToast(message: String) {
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
}

/**
 * Extension function untuk set background color dari resource
 */
fun View.setBackgroundColorRes(colorRes: Int) {
    this.setBackgroundColor(ContextCompat.getColor(this.context, colorRes))
}

/**
 * Extension function untuk animate fade in
 */
fun View.fadeIn(duration: Long = 300) {
    this.alpha = 0f
    this.show()
    this.animate()
        .alpha(1f)
        .setDuration(duration)
        .setListener(null)
}

/**
 * Extension function untuk animate fade out
 */
fun View.fadeOut(duration: Long = 300) {
    this.animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            this.hide()
        }
}

/**
 * Extension function untuk set click dengan debounce
 */
fun View.setDebouncedClickListener(
    debounceTime: Long = 600,
    onClick: (View) -> Unit,
) {
    this.setOnClickListener(
        object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (System.currentTimeMillis() - lastClickTime < debounceTime) return
                lastClickTime = System.currentTimeMillis()
                onClick(v)
            }
        },
    )
}

/**
 * Extension function untuk set safe click listener
 */
fun View.setSafeClickListener(onSafeClick: (View) -> Unit) {
    setDebouncedClickListener { view ->
        try {
            onSafeClick(view)
        } catch (e: Exception) {
            e.printStackTrace()
            view.showLongToast("Terjadi kesalahan, coba lagi")
        }
    }
}

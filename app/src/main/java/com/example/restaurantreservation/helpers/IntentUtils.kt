package com.example.restaurantreservation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.example.restaurantreservation.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class untuk menangani berbagai jenis Implicit Intent
 */
object IntentUtils {

    // Constants untuk Implicit Intent
    private const val RESTAURANT_PHONE = "+62123456789"
    private const val RESTAURANT_WEBSITE = "https://www.restaurantlezatselalu.com"
    private const val RESTAURANT_EMAIL = "info@restaurantlezatselalu.com"
    private const val RESTAURANT_LATITUDE = -6.2088
    private const val RESTAURANT_LONGITUDE = 106.8456
    private const val RESTAURANT_NAME = "Restoran Lezat Selalu"

    /**
     * 1. Implicit Intent untuk membuka Google Maps
     */
    fun openMaps(context: Context, address: String = RESTAURANT_NAME) {
        try {
            // Encode address untuk URL
            val encodedAddress = Uri.encode(address)

            // Coba buka dengan Google Maps app
            val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                // Fallback ke browser dengan Google Maps web
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedAddress")
                )
                if (webIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(webIntent)
                } else {
                    Toast.makeText(context, "Tidak ada aplikasi maps yang terinstall!", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error membuka maps: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 2. Implicit Intent untuk menelpon restoran
     */
    fun callRestaurant(context: Context, phoneNumber: String = RESTAURANT_PHONE) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Tidak ada aplikasi telepon yang terinstall!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error membuka telepon: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 3. Implicit Intent untuk membuka website restoran
     */
    fun openWebsite(context: Context, url: String = RESTAURANT_WEBSITE) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Tidak ada aplikasi browser yang terinstall!", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error membuka website: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 4. Implicit Intent untuk mengirim email konfirmasi
     */
    fun sendConfirmationEmail(context: Context, reservation: Reservation, email: String = RESTAURANT_EMAIL) {
        try {
            val subject = "Konfirmasi Reservasi - ${reservation.nama}"
            val body = """
                Dear $RESTAURANT_NAME,
                
                Saya ingin mengkonfirmasi reservasi dengan detail berikut:
                
                Nama: ${reservation.nama}
                Jumlah Orang: ${reservation.jumlahOrang}
                Tanggal: ${reservation.tanggal}
                Waktu: ${reservation.waktu}
                Meja: ${reservation.meja}
                Status: ${reservation.status}
                
                Terima kasih.
                
                Hormat kami,
                ${reservation.nama}
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback ke generic SEND intent
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }

                if (fallbackIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(Intent.createChooser(fallbackIntent, "Pilih aplikasi email"))
                } else {
                    Toast.makeText(context, "Tidak ada aplikasi email yang terinstall!", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error mengirim email: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 5. Implicit Intent untuk membagikan detail reservasi
     */
    fun shareReservation(context: Context, reservation: Reservation) {
        try {
            val shareText = """
                🍽️ Konfirmasi Reservasi Restoran
                
                Saya telah membuat reservasi di $RESTAURANT_NAME:
                
                👤 Nama: ${reservation.nama}
                👥 Jumlah Orang: ${reservation.jumlahOrang}
                📅 Tanggal: ${reservation.tanggal}
                ⏰ Waktu: ${reservation.waktu}
                🪑 Meja: ${reservation.meja}
                ✅ Status: ${reservation.status}
                
                Terima kasih!
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Konfirmasi Reservasi Restoran")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            context.startActivity(Intent.createChooser(intent, "Bagikan reservasi melalui"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error membagikan reservasi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 6. Implicit Intent untuk menambahkan ke kalender
     */
    fun addToCalendar(context: Context, reservation: Reservation) {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val eventDate = dateFormat.parse("${reservation.tanggal} ${reservation.waktu}")

            eventDate?.let { date ->
                val beginTime = date.time
                val endTime = beginTime + (2 * 60 * 60 * 1000) // 2 jam kemudian

                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.Events.TITLE, "Reservasi Restoran - ${reservation.nama}")
                    putExtra(CalendarContract.Events.DESCRIPTION,
                        "Reservasi untuk ${reservation.jumlahOrang} orang di meja ${reservation.meja}")
                    putExtra(CalendarContract.Events.EVENT_LOCATION, RESTAURANT_NAME)
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                    putExtra(CalendarContract.Events.ALL_DAY, false)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Tidak ada aplikasi kalender yang terinstall!", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(context, "Format tanggal tidak valid!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error menambahkan ke kalender: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 7. Implicit Intent untuk membuka WhatsApp
     */
    fun openWhatsApp(context: Context, reservation: Reservation) {
        try {
            val message = """
                Halo $RESTAURANT_NAME,
                
                Saya ingin konfirmasi reservasi:
                Nama: ${reservation.nama}
                Orang: ${reservation.jumlahOrang}
                Tanggal: ${reservation.tanggal}
                Waktu: ${reservation.waktu}
                Meja: ${reservation.meja}
                
                Terima kasih.
            """.trimIndent()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$RESTAURANT_PHONE?text=${Uri.encode(message)}")
                setPackage("com.whatsapp")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // WhatsApp tidak terinstall, buka browser
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://web.whatsapp.com/send?phone=$RESTAURANT_PHONE&text=${Uri.encode(message)}")
                }
                if (webIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(webIntent)
                } else {
                    Toast.makeText(context, "WhatsApp tidak terinstall!", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
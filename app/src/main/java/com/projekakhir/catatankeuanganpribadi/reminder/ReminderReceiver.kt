package com.projekakhir.catatankeuanganpribadi.receiver

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.activities.MainActivity

/**
 * ReminderReceiver adalah BroadcastReceiver yang digunakan untuk menampilkan
 * notifikasi harian agar pengguna tidak lupa mencatat transaksi keuangannya.
 * Receiver ini dipicu oleh AlarmManager.
 */
class ReminderReceiver : BroadcastReceiver() {

    /**
     * Fungsi ini dipanggil saat AlarmManager mengirim broadcast.
     * Notifikasi akan dibuat dan ditampilkan kepada pengguna.
     *
     * @param context Konteks aplikasi.
     * @param intent Intent yang diterima dari alarm (tidak digunakan dalam kasus ini).
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {

        // Intent untuk membuka MainActivity saat notifikasi diklik
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent agar notifikasi bisa membuka MainActivity
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Bangun notifikasi menggunakan NotificationCompat
        val builder = NotificationCompat.Builder(context, "sidompet_reminder")
            .setSmallIcon(R.drawable.ic_notification) // Ikon notifikasi
            .setContentTitle("Pengingat Harian") // Judul notifikasi
            .setContentText("Jangan lupa mencatat transaksi hari ini di SiDompet!") // Isi pesan
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas tinggi (muncul segera)
            .setContentIntent(pendingIntent) // Aksi saat notifikasi diklik
            .setAutoCancel(true) // Hapus notifikasi saat diklik

        // Tampilkan notifikasi jika diizinkan (khusus Android 13+ perlu cek izin)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        ) {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(100, builder.build()) // ID notifikasi: 100
        }
    }
}

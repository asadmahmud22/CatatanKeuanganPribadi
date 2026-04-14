package com.projekakhir.catatankeuanganpribadi.activities

// Import library yang dibutuhkan
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.db.DBHelper
import com.projekakhir.catatankeuanganpribadi.receiver.ReminderReceiver
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    // Deklarasi variabel untuk komponen UI dan database
    private lateinit var db: SQLiteDatabase
    private lateinit var tvPemasukan: TextView
    private lateinit var tvPengeluaran: TextView
    private lateinit var tvSelisih: TextView
    private lateinit var summaryText: TextView
    private lateinit var btnTambah: Button
    private lateinit var btnLihat: Button
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var ivAbout: ImageView
    private lateinit var greetingText: TextView
    private lateinit var greetingEmail: TextView
    private lateinit var ivProfile: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi database
        db = DBHelper(this).readableDatabase

        // Menghubungkan variabel dengan komponen UI dari layout
        tvPemasukan = findViewById(R.id.tvPemasukan)
        tvPengeluaran = findViewById(R.id.tvPengeluaran)
        tvSelisih = findViewById(R.id.tvSelisih)
        summaryText = findViewById(R.id.summaryText)
        btnTambah = findViewById(R.id.btnTambah)
        btnLihat = findViewById(R.id.btnLihat)
        bottomNav = findViewById(R.id.bottomNav)
        ivAbout = findViewById(R.id.ivAbout)
        greetingText = findViewById(R.id.greetingText)
        greetingEmail = findViewById(R.id.greetingEmail)
        ivProfile = findViewById(R.id.ivProfile)

        // Menonaktifkan aksi klik pada gambar profil
        ivProfile.isEnabled = false
        ivProfile.isClickable = false
        ivProfile.setOnTouchListener { _, _ -> true }

        // Aksi ketika ikon About ditekan
        ivAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Aksi ketika tombol tambah ditekan
        btnTambah.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Aksi ketika tombol lihat ditekan
        btnLihat.setOnClickListener {
            startActivity(Intent(this, TransactionListActivity::class.java))
        }

        // Navigasi bawah (Bottom Navigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_activity -> {
                    navigateTo(this@MainActivity, TransactionListActivity::class.java)
                    true
                }
                R.id.nav_profile -> {
                    navigateTo(this@MainActivity, ProfileActivity::class.java)
                    true
                }
                else -> false
            }
        }

        // Minta izin notifikasi khusus Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        // Buat Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pengingat Harian"
            val descriptionText = "Channel untuk pengingat harian SiDompet"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("sidompet_reminder", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: android.app.NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Mengatur warna status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // Menjalankan pengingat harian & update tampilan utama
        setupDailyReminder()
        updateSummary()
        updateUserInfo()
    }

    private fun setDailyReminder(context: Context) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val triggerTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20) // jam 20.00 malam
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        // Alarm harian
        alarmManager.setInexactRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            triggerTime,
            android.app.AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun updateUserInfo() {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", "User")
        val email = prefs.getString("email", "user@example.com")
        val imagePath = prefs.getString("profile_image", null)

        greetingText.text = "WELCOME, $nama"
        greetingEmail.text = email

        if (!imagePath.isNullOrEmpty()) {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    ivProfile.setImageURI(Uri.fromFile(file))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Gagal memuat gambar profil", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDailyReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sidompet_reminder",
                "Pengingat Harian",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Pengingat harian untuk mencatat transaksi"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun updateSummary() {
        val cursor = db.rawQuery("SELECT type, SUM(amount) FROM transaksi GROUP BY type", null)
        var pemasukan = 0
        var pengeluaran = 0

        while (cursor.moveToNext()) {
            val type = cursor.getString(0)
            val total = cursor.getInt(1)
            if (type == "pemasukan") pemasukan = total else pengeluaran = total
        }
        cursor.close()

        tvPemasukan.text = "Pemasukan\nRp$pemasukan"
        tvPengeluaran.text = "Pengeluaran\nRp$pengeluaran"
        tvSelisih.text = "Rp${pemasukan - pengeluaran}"

        val summaryCursor = db.rawQuery("""
            SELECT 
                SUM(CASE WHEN type = 'pemasukan' THEN amount ELSE 0 END) AS totalPemasukan,
                SUM(CASE WHEN type = 'pengeluaran' THEN amount ELSE 0 END) AS totalPengeluaran 
            FROM transaksi
        """.trimIndent(), null)

        if (summaryCursor.moveToFirst()) {
            val totalPemasukan = summaryCursor.getInt(summaryCursor.getColumnIndexOrThrow("totalPemasukan"))
            val totalPengeluaran = summaryCursor.getInt(summaryCursor.getColumnIndexOrThrow("totalPengeluaran"))
            val selisih = totalPemasukan - totalPengeluaran

            summaryText.text = when {
                selisih > 0 -> "Pemasukan melebihi pengeluaran sebesar Rp$selisih"
                selisih < 0 -> "Pengeluaran melebihi pemasukan sebesar Rp${-selisih}"
                else -> "Pemasukan dan pengeluaran seimbang"
            }
        }
        summaryCursor.close()
    }

    override fun onResume() {
        super.onResume()
        updateSummary()
        updateUserInfo()
    }

    private fun navigateTo(activity: AppCompatActivity, destination: Class<out AppCompatActivity>) {
        if (activity::class.java != destination) {
            val intent = Intent(activity, destination)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        }
    }
}

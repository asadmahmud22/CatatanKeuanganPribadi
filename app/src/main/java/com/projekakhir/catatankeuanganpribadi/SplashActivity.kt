package com.projekakhir.catatankeuanganpribadi

// Import library Android yang dibutuhkan
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.projekakhir.catatankeuanganpribadi.activities.MainActivity

/**
 * SplashActivity adalah activity awal yang tampil saat aplikasi dibuka pertama kali.
 * Menampilkan tombol untuk masuk ke halaman utama aplikasi (MainActivity).
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Fungsi onCreate dipanggil saat activity dibuat pertama kali.
     * Di sini layout ditampilkan dan tombol "Masuk" dihubungkan ke aksi pindah ke MainActivity.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menghubungkan activity dengan layout XML activity_splash.xml
        setContentView(R.layout.activity_splash)

        // Menghubungkan tombol "Masuk" dari layout dengan variabel di kode
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)

        // Menambahkan listener agar saat tombol ditekan, pengguna diarahkan ke halaman utama
        btnMasuk.setOnClickListener {
            // Membuat intent untuk berpindah ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))

            // Menutup SplashActivity agar tidak bisa kembali ke splash saat menekan tombol kembali
            finish()
        }


        // Inisialisasi tombol logout jika memang tersedia di layout
        val btnLogout = findViewById<Button?>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()

                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
}

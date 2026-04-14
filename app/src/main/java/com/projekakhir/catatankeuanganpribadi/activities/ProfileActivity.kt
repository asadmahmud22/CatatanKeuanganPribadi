package com.projekakhir.catatankeuanganpribadi.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.SplashActivity
import java.io.File
import java.io.FileOutputStream

/**
 * ProfileActivity memungkinkan pengguna untuk melihat dan mengubah informasi profil mereka
 * termasuk nama, email, dan gambar profil.
 */
class ProfileActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSimpan: Button
    private lateinit var ivProfile: ImageView
    private var imageUri: Uri? = null

    companion object {
        const val REQUEST_IMAGE_PICK = 100
        const val PROFILE_IMAGE_NAME = "profile_image.jpg"

        /**
         * Fungsi statis untuk menyalin gambar ke penyimpanan internal.
         * Digunakan untuk menyimpan foto profil pengguna secara permanen.
         */
        private fun copyImageToInternalStorage(profileActivity: ProfileActivity, uri: Uri): String? {
            return try {
                val inputStream = profileActivity.contentResolver.openInputStream(uri)
                val file = File(profileActivity.filesDir, PROFILE_IMAGE_NAME)
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Fungsi utama yang dijalankan saat activity dibuat.
     * Mengatur tampilan profil, mengambil data pengguna dari SharedPreferences,
     * dan menetapkan aksi pada tombol dan navigasi.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi elemen UI
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        btnSimpan = findViewById(R.id.btnSimpanProfile)
        ivProfile = findViewById(R.id.ivProfile)

        // Ambil data user dari SharedPreferences
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nama = prefs.getString("nama", "")
        val email = prefs.getString("email", "")
        val imagePath = prefs.getString("profile_image", null)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Tampilkan nama dan email yang disimpan
        etNama.setText(nama)
        etEmail.setText(email)

        // Jika ada gambar profil, tampilkan di ImageView
        if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            if (file.exists()) {
                ivProfile.setImageURI(Uri.fromFile(file))
            }
        }

        // Ketika pengguna menekan gambar profil, buka galeri untuk memilih gambar baru
        ivProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // Simpan data profil ketika tombol "Simpan" ditekan
        btnSimpan.setOnClickListener {
            val newNama = etNama.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()

            // Validasi nama tidak boleh kosong
            if (newNama.isNotEmpty()) {
                prefs.edit()
                    .putString("nama", newNama)
                    .putString("email", newEmail)
                    .apply()

                Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show()

                // Kembali ke MainActivity setelah menyimpan
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            // Hapus data dari SharedPreferences
            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply() // Menghapus semua data pengguna

            // Arahkan ke SplashActivity (atau LoginActivity jika ada)
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Tutup activity saat ini
        }

        // Navigasi bawah (bottom navigation)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile

        // Aksi ketika item navigasi ditekan
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_activity -> {
                    startActivity(Intent(this, TransactionListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true // Sudah berada di halaman profil
                else -> false
            }
        }
    }

    /**
     * Fungsi ini menyalin gambar yang dipilih dari galeri ke penyimpanan internal aplikasi.
     */
    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = PROFILE_IMAGE_NAME
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Menangani hasil dari aktivitas pemilihan gambar.
     * Jika berhasil, gambar disalin ke storage internal dan ditampilkan di ImageView.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedUri = data?.data
            selectedUri?.let {
                val savedImagePath = copyImageToInternalStorage(it)
                if (savedImagePath != null) {
                    val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("profile_image", savedImagePath).apply()

                    ivProfile.setImageURI(Uri.fromFile(File(savedImagePath)))
                } else {
                    Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

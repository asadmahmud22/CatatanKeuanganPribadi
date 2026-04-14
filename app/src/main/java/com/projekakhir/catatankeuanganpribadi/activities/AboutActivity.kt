package com.projekakhir.catatankeuanganpribadi.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.projekakhir.catatankeuanganpribadi.R

/**
 * AboutActivity adalah activity yang menampilkan informasi tentang aplikasi,
 * seperti deskripsi singkat, versi, atau tim pengembang.
 */
class AboutActivity : AppCompatActivity() {

    /**
     * Fungsi onCreate dipanggil saat activity dibuat.
     * Di sini layout ditampilkan dan tombol kembali diatur fungsinya.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menentukan layout yang digunakan untuk activity ini
        setContentView(R.layout.activity_about)

        // Menghubungkan komponen ImageView (ikon kembali) dari layout
        val ivBack = findViewById<ImageView>(R.id.ivBack)

        // Menambahkan aksi saat ikon kembali ditekan
        ivBack.setOnClickListener {
            // Menutup activity dan kembali ke halaman sebelumnya
            finish()
        }
    }
}

package com.projekakhir.catatankeuanganpribadi.activities

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.db.DBHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity untuk menambahkan transaksi baru ke database.
 * Pengguna dapat mengisi jumlah uang, deskripsi, tanggal, kategori, dan jenis transaksi.
 */
class AddTransactionActivity : AppCompatActivity() {

    // Deklarasi semua komponen UI
    private lateinit var etJumlah: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etTanggal: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var rbPemasukan: RadioButton
    private lateinit var rbPengeluaran: RadioButton
    private lateinit var btnSimpan: Button
    private lateinit var ivBack: ImageView

    // Daftar kategori yang akan ditampilkan di Spinner
    private val kategoriList = listOf("Umum", "Makanan", "Transportasi", "Gaji", "Tagihan", "Lainnya")

    /**
     * Fungsi yang dijalankan saat activity pertama kali dibuat.
     * Digunakan untuk menginisialisasi tampilan dan logika dasar.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Inisialisasi komponen UI berdasarkan ID dari layout
        etJumlah = findViewById(R.id.etJumlah)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        etTanggal = findViewById(R.id.etTanggal)
        spinnerKategori = findViewById(R.id.spinnerKategori)
        rbPemasukan = findViewById(R.id.rbPemasukan)
        rbPengeluaran = findViewById(R.id.rbPengeluaran)
        btnSimpan = findViewById(R.id.btnSimpan)
        ivBack = findViewById(R.id.ivBack)

        // Mengatur tanggal default sebagai tanggal hari ini
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etTanggal.setText(dateFormat.format(calendar.time))

        // Menampilkan DatePickerDialog saat input tanggal diklik
        etTanggal.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Menampilkan dialog pemilih tanggal
            DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(y, m, d)
                etTanggal.setText(dateFormat.format(selectedDate.time))
            }, year, month, day).show()
        }

        // Mengatur adapter spinner kategori
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        spinnerKategori.adapter = adapter

        // Aksi saat tombol kembali ditekan
        ivBack.setOnClickListener {
            finish() // Tutup activity
        }

        // Aksi saat tombol simpan ditekan
        btnSimpan.setOnClickListener {
            // Menentukan jenis transaksi berdasarkan radio button
            val type = if (rbPemasukan.isChecked) "pemasukan" else "pengeluaran"

            // Ambil nilai dari input pengguna
            val amount = etJumlah.text.toString().toIntOrNull()
            val description = etDeskripsi.text.toString()
            val date = etTanggal.text.toString()
            val kategori = spinnerKategori.selectedItem.toString()

            // Validasi input: semua field harus terisi dan amount harus angka
            if (amount != null && description.isNotEmpty() && date.isNotEmpty()) {
                val db = DBHelper(this).writableDatabase

                // Menyusun data yang akan disimpan dalam bentuk ContentValues
                val values = ContentValues().apply {
                    put("type", type)
                    put("amount", amount)
                    put("description", description)
                    put("date", date)
                    put("kategori", kategori)
                }

                // Menyimpan data ke database
                val result = db.insert("transaksi", null, values)

                // Beri notifikasi berhasil/gagal menyimpan
                if (result != -1L) {
                    Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish() // Tutup activity dan kembali
                } else {
                    Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

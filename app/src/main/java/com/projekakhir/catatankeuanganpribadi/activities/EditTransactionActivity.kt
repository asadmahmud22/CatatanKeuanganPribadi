package com.projekakhir.catatankeuanganpribadi.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.db.DBHelper

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var etJumlah: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etTanggal: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var rgJenis: RadioGroup
    private lateinit var btnUpdate: Button
    private lateinit var ivBack: ImageView
    private var transaksiId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        etJumlah = findViewById(R.id.etJumlah)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        etTanggal = findViewById(R.id.etTanggal)
        spinnerKategori = findViewById(R.id.spinnerKategori)
        rgJenis = findViewById(R.id.rgJenis)
        btnUpdate = findViewById(R.id.btnUpdate)
        ivBack = findViewById(R.id.ivBack)

        // Mengatur adapter spinner kategori
        val kategoriList = resources.getStringArray(R.array.kategori_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kategoriList)
        spinnerKategori.adapter = adapter

        transaksiId = intent.getIntExtra("id", -1)
        if (transaksiId != -1) {
            tampilkanData()
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnUpdate.setOnClickListener {
            updateData()
        }

        // Aksi saat tombol kembali ditekan
        ivBack.setOnClickListener {
            finish() // Tutup activity
        }
    }

    private fun tampilkanData() {
        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transaksi WHERE id = ?", arrayOf(transaksiId.toString()))
        if (cursor.moveToFirst()) {
            val jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("amount"))
            val deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val tanggal = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"))
            val jenis = cursor.getString(cursor.getColumnIndexOrThrow("type"))

            etJumlah.setText(jumlah.toString())
            etDeskripsi.setText(deskripsi)
            etTanggal.setText(tanggal)

            val kategoriArray = resources.getStringArray(R.array.kategori_array)
            val indexKategori = kategoriArray.indexOf(kategori)
            if (indexKategori >= 0) spinnerKategori.setSelection(indexKategori)

            if (jenis == "Pemasukan") {
                rgJenis.check(R.id.rbPemasukan)
            } else {
                rgJenis.check(R.id.rbPengeluaran)
            }
        }
        cursor.close()
    }

    private fun updateData() {
        val jumlah = etJumlah.text.toString().toIntOrNull()
        val deskripsi = etDeskripsi.text.toString()
        val tanggal = etTanggal.text.toString()
        val kategori = spinnerKategori.selectedItem.toString()
        val jenis = if (rgJenis.checkedRadioButtonId == R.id.rbPemasukan) "Pemasukan" else "Pengeluaran"

        if (jumlah == null || deskripsi.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val db = DBHelper(this).writableDatabase
        val sql = "UPDATE transaksi SET amount=?, description=?, date=?, kategori=?, type=? WHERE id=?"
        db.execSQL(sql, arrayOf(jumlah, deskripsi, tanggal, kategori, jenis, transaksiId))

        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }
}

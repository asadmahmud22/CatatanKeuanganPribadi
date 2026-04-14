package com.projekakhir.catatankeuanganpribadi.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DBHelper adalah class yang mengatur pembuatan dan pengelolaan database SQLite lokal.
 * Class ini mewarisi SQLiteOpenHelper.
 *
 * Database ini menyimpan data transaksi keuangan (pemasukan & pengeluaran).
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    companion object {
        // Nama database
        const val DB_NAME = "keuangan.db"

        // Nama tabel yang digunakan
        const val TABLE_NAME = "transaksi"

        // SQL untuk membuat tabel transaksi
        val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,    -- ID transaksi (auto increment)
                type TEXT,                                -- Jenis transaksi: pemasukan / pengeluaran
                amount INTEGER,                           -- Jumlah uang transaksi
                description TEXT,                         -- Deskripsi transaksi
                date TEXT,                                -- Tanggal transaksi (disimpan dalam bentuk teks)
                kategori TEXT                             -- Kategori transaksi (misalnya: Makan, Transportasi, dll.)
            )
        """.trimIndent()
    }

    /**
     * Fungsi ini dipanggil saat database pertama kali dibuat.
     * Digunakan untuk mengeksekusi perintah SQL untuk membuat tabel.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    /**
     * Fungsi ini dipanggil saat terjadi upgrade versi database.
     * Dalam implementasi ini, jika versi berubah, maka tabel lama dihapus dan dibuat ulang.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Menghapus tabel jika sudah ada
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")

        // Membuat ulang tabel
        onCreate(db)
    }
}

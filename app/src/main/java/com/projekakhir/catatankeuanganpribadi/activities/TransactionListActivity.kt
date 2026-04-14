package com.projekakhir.catatankeuanganpribadi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.projekakhir.catatankeuanganpribadi.R
import com.projekakhir.catatankeuanganpribadi.db.DBHelper

class TransactionListActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var listContainer: LinearLayout
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        ivBack = findViewById(R.id.ivBack)
        listContainer = findViewById(R.id.listContainer)
        bottomNav = findViewById(R.id.bottomNav)

        ivBack.setOnClickListener { finish() }
        bottomNav.selectedItemId = R.id.nav_activity

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_activity -> true
                R.id.nav_profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }

        tampilkanTransaksi()
    }

    override fun onResume() {
        super.onResume()
        tampilkanTransaksi()
    }

    private fun tampilkanTransaksi() {
        listContainer.removeAllViews()

        val db = DBHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transaksi ORDER BY date DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val tanggal = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val jenis = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                val jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("amount"))
                val deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"))

                val tvItem = TextView(this).apply {
                    text = "$tanggal | $jenis | Rp$jumlah\n$deskripsi - $kategori"
                    setPadding(24, 16, 24, 16)
                    textSize = 14f
                    setTextColor(resources.getColor(android.R.color.black))
                    setBackgroundResource(R.drawable.bg_item_transaksi)

                    setOnClickListener {
                        tampilkanDialogAksi(id)
                    }
                }

                val garis = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2
                    ).apply {
                        setMargins(0, 12, 0, 12)
                    }
                    setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                }

                listContainer.addView(tvItem)
                listContainer.addView(garis)
            } while (cursor.moveToNext())
        } else {
            val kosong = TextView(this).apply {
                text = "Belum ada transaksi."
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.white))
                setPadding(24, 24, 24, 24)
            }
            listContainer.addView(kosong)
        }

        cursor.close()
    }

    private fun tampilkanDialogAksi(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Aksi")
        builder.setItems(arrayOf("Edit", "Hapus")) { _, which ->
            when (which) {
                0 -> {
                    val intent = Intent(this, EditTransactionActivity::class.java)
                    intent.putExtra("id", id)
                    startActivity(intent)
                }
                1 -> {
                    hapusTransaksi(id)
                }
            }
        }
        builder.show()
    }

    private fun hapusTransaksi(id: Int) {
        val db = DBHelper(this).writableDatabase
        db.delete("transaksi", "id = ?", arrayOf(id.toString()))
        Toast.makeText(this, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
        tampilkanTransaksi()
    }
}

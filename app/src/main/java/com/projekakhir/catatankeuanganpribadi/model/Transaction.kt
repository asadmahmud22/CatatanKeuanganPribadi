package com.projekakhir.catatankeuanganpribadi.model

/**
 * Data class Transaction merepresentasikan satu entitas transaksi keuangan.
 *
 * @property id ID unik dari transaksi, biasanya auto-increment dari database.
 * @property type Jenis transaksi, bisa bernilai "pemasukan" atau "pengeluaran".
 * @property amount Jumlah uang yang terlibat dalam transaksi (dalam satuan Rupiah).
 * @property description Keterangan atau catatan tambahan terkait transaksi.
 * @property date Tanggal saat transaksi dilakukan, dalam format "yyyy-MM-dd".
 */
data class Transaction(
    val id: Int,             // ID unik transaksi
    val type: String,        // "pemasukan" atau "pengeluaran"
    val amount: Int,         // Nominal transaksi
    val description: String, // Deskripsi transaksi
    val date: String         // Tanggal transaksi
)

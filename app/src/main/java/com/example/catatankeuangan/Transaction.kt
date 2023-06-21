package com.example.catatankeuangan

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "keuangan_db")
data class Transaction(
  val kategori: String,
  val keterangan: String,
  val tanggal: String,
  val jumlah: Int,
  val tipe: String,
  @PrimaryKey(autoGenerate = true)
  val id: Int
): Serializable

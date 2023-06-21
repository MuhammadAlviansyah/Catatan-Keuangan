package com.example.catatankeuangan

import androidx.room.*

@Dao
interface TransactionDao {
  @Query("SELECT * FROM keuangan_db ORDER BY id DESC")
  fun getAll(): List<Transaction>

  @Insert
  fun insertAll(vararg transaction: Transaction)

  @Delete
  fun delete(transaction: Transaction)

  @Update
  fun update(vararg transaction: Transaction)
}
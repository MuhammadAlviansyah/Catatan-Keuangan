package com.example.catatankeuangan

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.catatankeuangan.databinding.ActivityEditTransactionBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditTransactionActivity : AppCompatActivity() {
  private lateinit var binding: ActivityEditTransactionBinding
  private lateinit var transaction: Transaction

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityEditTransactionBinding.inflate(layoutInflater)
    setContentView(binding.root)
    supportActionBar?.hide()

    transaction = intent.getSerializableExtra("keuangan_db") as Transaction

    binding.jumlahInput.setText(transaction.jumlah.toString())
    binding.kategoriInput.setText(transaction.kategori.toString())
    binding.keteranganInput.setText(transaction.keterangan.toString())
    binding.tanggalInput.setText(transaction.tanggal.toString())

    binding.rootView.setOnClickListener {
      this.window.decorView.clearFocus()

      val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(it.windowToken, 0)
    }

    binding.jumlahInput.addTextChangedListener {
      if (it!!.count() > 0)
        binding.jumlahLayout.error = null
    }

    binding.kategoriInput.addTextChangedListener {
      if (it!!.count() > 0)
        binding.kategoriLayout.error = null
    }

    binding.keteranganInput.addTextChangedListener {
      if (it!!.count() > 0)
        binding.keteranganLayout.error = null
    }

    binding.tanggalInput.addTextChangedListener {
      if (it!!.count() > 0)
        binding.tanggalLayout.error = null
    }

    binding.editTransactionBtn.setOnClickListener{
      val kategori = binding.kategoriInput.text.toString()
      val keterangan = binding.keteranganInput.text.toString()
      val tanggal = binding.tanggalInput.text.toString()
      val jumlah = binding.jumlahInput.text.toString().toIntOrNull()

      if (jumlah == null)
        binding.jumlahLayout.error = "Please enter a valid nominal"
      else if (kategori.isEmpty())
        binding.kategoriLayout.error = "Please enter a valid category"
      else if (keterangan.isEmpty())
        binding.keteranganLayout.error = "Please enter a valid description"
      else if (tanggal.isEmpty())
        binding.tanggalLayout.error = "Please enter a valid date"
      else {
        val transaction = Transaction(kategori, keterangan, tanggal, jumlah, transaction.tipe, transaction.id)
        update(transaction)
      }
    }

    binding.backBtn.setOnClickListener {
      finish()
    }
  }

  private fun update(transaction: Transaction) {
    val db = Room.databaseBuilder(this, TransactionDatabase::class.java, "keuangan_db").build()

    GlobalScope.launch {
      db.transactionDao().update(transaction)
      finish()
    }
  }
}
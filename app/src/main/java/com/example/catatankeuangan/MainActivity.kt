package com.example.catatankeuangan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.catatankeuangan.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
  private lateinit var transactions: List<Transaction>
  private lateinit var transactionAdapter: TransactionAdapter
  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var deleteTransaction: Transaction
  private lateinit var oldTransactions: List<Transaction>
  private lateinit var binding: ActivityMainBinding
  private lateinit var db: TransactionDatabase

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    supportActionBar?.hide()

    transactions = arrayListOf()

    transactionAdapter = TransactionAdapter(transactions)
    linearLayoutManager = LinearLayoutManager(this)

    db = Room.databaseBuilder(this, TransactionDatabase::class.java, "keuangan_db").build()

    binding.recyclerview.apply {
      adapter = transactionAdapter
      layoutManager = linearLayoutManager
    }

    val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
      ): Boolean {
        return false
      }

      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        deleteTransaction(transactions[viewHolder.adapterPosition])
      }
    }

    val swipeHelper = ItemTouchHelper(itemTouchHelper)
    swipeHelper.attachToRecyclerView(binding.recyclerview)

    binding.addBtn.setOnClickListener {
      val intent = Intent(this, AddTransactionActivity::class.java)
      startActivity(intent)
    }
  }

  private fun updateUI() {
    if (transactions.isNotEmpty()) {
      binding.lyNotFound.visibility = View.GONE
    } else {
      binding.lyNotFound.visibility = View.VISIBLE
    }
  }

  private fun fetchAll(){
    GlobalScope.launch {
      transactions = db.transactionDao().getAll()

      runOnUiThread {
        updateUI()
        updateDashboard()
        transactionAdapter.setData(transactions)
      }
    }
  }

  private fun updateDashboard() {
    val formatter =  DecimalFormat("#,###")
    val income = transactions.filter { it.tipe == "pemasukan" }.map { it.jumlah }.sum()
    val outcome = transactions.filter { it.tipe == "pengeluaran" }.map { it.jumlah }.sum()
    val balance = income - outcome

    binding.balance.text = "Rp " + formatter.format(balance).replace(",", ".")
    binding.income.text = "Rp " + formatter.format(income).replace(",", ".")
    binding.outcome.text = "Rp " + formatter.format(outcome).replace(",", ".")
  }

  private fun showSnackbar() {
    val view = findViewById<View>(R.id.coordinator)
    val snackbar = Snackbar.make(view, "Transaction deleted!", Snackbar.LENGTH_LONG)
    snackbar.setAction("Undo") {
      undoDelete()
    }
      .setActionTextColor(ContextCompat.getColor(this, R.color.white))
      .setTextColor(ContextCompat.getColor(this, R.color.white))
      .show()
  }

  private fun undoDelete() {
    GlobalScope.launch {
      db.transactionDao().insertAll(deleteTransaction)
      transactions = oldTransactions

      runOnUiThread {
        updateUI()
        transactionAdapter.setData(transactions)
        updateDashboard()
      }
    }
  }

  private fun deleteTransaction(transaction: Transaction) {
    deleteTransaction = transaction
    oldTransactions = transactions

    GlobalScope.launch {
      db.transactionDao().delete(transaction)
      transactions = transactions.filter { it.id != transaction.id }
      runOnUiThread {
        updateUI()
        updateDashboard()
        transactionAdapter.setData(transactions)
        showSnackbar()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    fetchAll()
  }
}
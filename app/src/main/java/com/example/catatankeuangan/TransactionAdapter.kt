package com.example.catatankeuangan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TransactionAdapter(private var transactions: List<Transaction>): RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {
  class TransactionHolder(view: View): RecyclerView.ViewHolder(view){
    val kategori :TextView = view.findViewById(R.id.kategori)
    val keterangan :TextView = view.findViewById(R.id.keterangan)
    val tanggal :TextView = view.findViewById(R.id.tanggal)
    val jumlah :TextView = view.findViewById(R.id.jumlah)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
    return TransactionHolder(view)
  }

  override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
    val transaction = transactions[position]
    val context = holder.jumlah.context
    val formatter = DecimalFormat("#,###")

    if(transaction.tipe == "pemasukan"){
      holder.jumlah.text = "+ Rp " + formatter.format(transaction.jumlah).replace(",", ".")
      holder.jumlah.setTextColor(ContextCompat.getColor(context, R.color.income))
    }else {
      holder.jumlah.text = "- Rp " + formatter.format(Math.abs(transaction.jumlah)).replace(",", ".")
      holder.jumlah.setTextColor(ContextCompat.getColor(context, R.color.outcome))
    }
    holder.kategori.text = transaction.kategori
    holder.keterangan.text = transaction.keterangan
    holder.tanggal.text = transaction.tanggal

    holder.itemView.setOnClickListener {
      val intent = Intent(context, EditTransactionActivity::class.java)
      intent.putExtra("keuangan_db", transaction)
      context.startActivity(intent)
    }
  }

  override fun getItemCount(): Int {
    return transactions.size
  }

  fun setData(transactions: List<Transaction>){
    this.transactions = transactions
    notifyDataSetChanged()
  }
}
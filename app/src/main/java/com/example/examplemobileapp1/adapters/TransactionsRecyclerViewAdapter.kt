package com.example.examplemobileapp1.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.examplemobileapp1.databinding.ItemTransactionBinding
import com.example.examplemobileapp1.models.Transaction

class TransactionsRecyclerViewAdapter(private val itemList: List<Transaction>)
    : RecyclerView.Adapter<TransactionsRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(private val binding: ItemTransactionBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: String) = with(binding){

            Glide.with(binding.root).load(itemList[position].imageUrl).into(transactionImageView)

            transactionRecipientTextView.text = itemList[position].recipient
            transactionDescriptionTextView.text = itemList[position].description
            transactionAmountTextView.text = itemList[position].amount
            transactionCurrencyTextView.text = itemList[position].currency
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionsRecyclerViewAdapter.MyViewHolder {
         return MyViewHolder(ItemTransactionBinding
             .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: TransactionsRecyclerViewAdapter.MyViewHolder,
        position: Int
    ) {
        val item = itemList[position]
        holder.apply {
            bind(item.imageUrl!!)
            bind(item.recipient)
            bind(item.description)
            bind(item.amount)
            bind(item.currency)
        }
    }

    override fun getItemCount() = itemList.size
}
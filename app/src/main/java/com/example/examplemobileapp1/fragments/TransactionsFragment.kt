package com.example.examplemobileapp1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examplemobileapp1.R
import com.example.examplemobileapp1.adapters.TransactionsRecyclerViewAdapter
import com.example.examplemobileapp1.databinding.FragmentTransactionsBinding
import com.example.examplemobileapp1.models.Transaction

class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding
    private lateinit var transactionsRecyclerViewAdapter: TransactionsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding){
        transactionsRecyclerViewAdapter = TransactionsRecyclerViewAdapter(getData())
        transactionsRecyclerView.adapter = transactionsRecyclerViewAdapter
        transactionsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getData(): List<Transaction> {
        val itemList = ArrayList<Transaction>()
        itemList.add(Transaction("https://png.pngtree.com/element_our/png/20181108/transaction-line-icon-png_234015.jpg","recipient1","description1","1500","gel"))
        itemList.add(Transaction("https://i.pinimg.com/474x/18/60/f4/1860f4fedfc5a4bdc9053bee222c464d.jpg","recipient2","description1","1500","gel"))
        itemList.add(Transaction("","recipient3","description1","1500","gel"))
        itemList.add(Transaction("","recipient4","description1","1500","gel"))
        itemList.add(Transaction("","recipient5","description1","1500","gel"))
        return itemList
    }
}
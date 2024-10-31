package com.example.examplemobileapp1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.examplemobileapp1.R
import com.example.examplemobileapp1.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var _binding : FragmentMainBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonListeners()
    }
    private fun buttonListeners(){
        binding.apply {
            sendMoneyBtn.setOnClickListener{
                Navigation.findNavController(it).navigate(MainFragmentDirections.actionMainFragmentToChooseRecipientFragment())
            }
            viewBalanceBtn.setOnClickListener{
                Toast.makeText(context, "coming soon", Toast.LENGTH_SHORT).show()
            }
            viewTransactionBtn.setOnClickListener{
                Toast.makeText(context, "coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
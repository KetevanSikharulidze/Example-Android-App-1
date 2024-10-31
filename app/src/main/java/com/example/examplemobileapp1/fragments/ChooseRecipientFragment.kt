package com.example.examplemobileapp1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.examplemobileapp1.R
import com.example.examplemobileapp1.databinding.FragmentChooseRecipientBinding

class ChooseRecipientFragment : Fragment(R.layout.fragment_choose_recipient){
    private lateinit var _binding: FragmentChooseRecipientBinding
    private  val binding get()= _binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseRecipientBinding.inflate(inflater,container,false)
        listeners()
        return binding.root
    }

    private fun listeners(){
        binding.apply { 
            cancelBtn.setOnClickListener{
                Toast.makeText(context, "canceled transaction", Toast.LENGTH_SHORT).show()
            }
            nextBtn.setOnClickListener{
                val name = editText.text.toString()
                Navigation.findNavController(requireView())
                    .navigate(ChooseRecipientFragmentDirections
                        .actionChooseRecipientFragmentToConfirmationFragment2(name))
            }
        } 
            
    }
}
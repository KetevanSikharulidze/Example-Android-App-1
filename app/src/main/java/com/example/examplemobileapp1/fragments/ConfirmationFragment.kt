package com.example.examplemobileapp1.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.examplemobileapp1.R

class ConfirmationFragment : Fragment(R.layout.fragment_confirmation) {

    private val args: ConfirmationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.confirmationTextView).text=
            args.name
    }
}
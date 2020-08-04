package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bpapps.childprotector.R

class RegistrationFragment : Fragment() {

    private var actionBar: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar = (activity as AppCompatActivity).supportActionBar?.also { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }

        val callBack = (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(this) {
            val exit = true

            if (exit) {
                (activity as AppCompatActivity).finish()
            } else {
                Toast.makeText(requireContext(), "Exit = false", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

}
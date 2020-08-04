package com.bpapps.childprotector.view.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.MainActivity

private const val TAG = "TAG.SplashScreenFragment"

class SplashScreenFragment : Fragment() {

    private lateinit var tvCreator: AppCompatTextView
    private var isRegistered = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        tvCreator = view.findViewById(R.id.tvCreator)
        tvCreator.setOnClickListener {
            Toast.makeText(requireContext(), "tvCreator onClick", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(requireContext(), "override fun onResume() ", Toast.LENGTH_SHORT).show()

        val sharedPreferences =
            activity?.getSharedPreferences(
                MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE
            )

        isRegistered = sharedPreferences!!.getBoolean(
            MainActivity.PREFERENCES_IS_REGISTERED,
            false
        )

        Handler().postDelayed({
            if (isRegistered) {
                findNavController().navigate(R.id.action_splashScreenFragment_to_showChildrenFragment)
            } else {
                findNavController().navigate(R.id.action_splashScreenFragment_to_registrationFragment)
            }
        }, resources.getInteger(R.integer.splash_screen_number_of_milliseconds_shower).toLong())
    }

}
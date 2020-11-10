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
import com.bpapps.childprotector.model.classes.UserType
import com.bpapps.childprotector.view.MainActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.IllegalStateException

private const val TAG = "TAG.SplashScreenFragment"

class SplashScreenFragment : Fragment() {

    private lateinit var tvCreator: AppCompatTextView
    private var isRegistered = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        tvCreator = view.findViewById(R.id.tvCreator)
        tvCreator.setOnClickListener {
            Toast.makeText(requireContext(), "tvCreator onClick", Toast.LENGTH_SHORT).show()
        }

        FirebaseCrashlytics.getInstance().log("Higgs-Boson detected! Bailing out")
        throw RuntimeException("Test Crash") // Force a crash
//        throw IllegalStateException("crash for crashlytics setup purpose")
        return view
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).supportActionBar?.hide()

        val sharedPref =
            activity?.getSharedPreferences(
                MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE
            )

        isRegistered = sharedPref!!.getBoolean(
            MainActivity.PREFERENCES_IS_REGISTERED,
            false
        )

        Handler().postDelayed({
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)
            if (isRegistered) {
                when (sharedPref.getInt(MainActivity.PREFERENCES_USER_TYPE, UserType.CHILD)) {
                    UserType.CHILD ->
                        navHostFragment?.findNavController()
                            ?.navigate(R.id.action_splashScreenFragment_to_childViewFragment)
                   UserType.PARENT->
                       navHostFragment?.findNavController()
                           ?.navigate(R.id.action_splashScreenFragment_to_parentViewFragment)
                }
            } else {
                navHostFragment?.findNavController()
                    ?.navigate(R.id.action_splashScreenFragment_to_registrationUserTypeFragment)
            }
        }, resources.getInteger(R.integer.splash_screen_number_of_milliseconds_shower).toLong())
    }

}
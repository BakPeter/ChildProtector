package com.bpapps.childprotector.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bpapps.childprotector.R
import com.bpapps.childprotector.viewmodel.viewmodels.ChildProtectorViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        const val PREFERENCES_FILE_NAME: String = "com.bpapps.childprotector.PREFERENCES_FILE_NAME"
        const val PREFERENCES_IS_REGISTERED: String =
            "com.bpapps.childprotector.PREFERENCES_IS_REGISTERD"
        const val PREFERENCES_USER_TYPE = "com.bpapps.childprotector.PREFERENCES_USER_TYPE"
        const val PREFERENCES_CHILD_MONITORED = "com.bpapps.childprotector.PREFERENCES_CHILD_MONITORED"
    }

    private val viewModel by viewModels<ChildProtectorViewModel>()

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

    }


    override fun onSupportNavigateUp(): Boolean {
//        val navHostFragment = this.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = findNavController(R.id.nav_host_fragment)

        return navController!!.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}



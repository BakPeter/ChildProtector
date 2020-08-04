package com.bpapps.childprotector.view

import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bpapps.childprotector.R
import com.bpapps.childprotector.viewmodel.ChildProtectorViewModel

class MainActivity : AppCompatActivity() {
    companion object {
        const val PREFERENCES_FILE_NAME: String = "com.bpapps.childprotector.PREFERENCES_FILE_NAME"
        const val PREFERENCES_IS_REGISTERED: String = "com.bpapps.childprotector.PREFERENCES_IS_REGISTERD"
    }

    private lateinit var tvUserName: AppCompatTextView
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
        val navController = findNavController((R.id.nav_host_fragment))

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

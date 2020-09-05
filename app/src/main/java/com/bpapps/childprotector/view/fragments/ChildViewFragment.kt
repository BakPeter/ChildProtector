package com.bpapps.childprotector.view.fragments

import android.Manifest
import android.app.AppOpsManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.MainActivity
import com.bpapps.childprotector.viewmodel.services.AppJobService
import com.bpapps.childprotector.viewmodel.viewmodels.ChildViewModel


private const val TAG = "TAG.ChildViewFragment"

class ChildViewFragment : Fragment(), ChildViewModel.IMonitoringStatusChanged {
    companion object {
        const val JOB_ID = 101
        const val PERMISSION_LOCATION_CODE = 11
    }

    private val viewModel by viewModels<ChildViewModel>()

    private lateinit var btnStartMonitoring: AppCompatButton
    private lateinit var btnStopMonitoring: AppCompatButton
    private lateinit var tvChildName: AppCompatTextView
    private lateinit var ivChildPic: AppCompatImageView
    private lateinit var ivIsMonitored: AppCompatImageView

    private var appBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref =
            activity?.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        sharedPref?.getBoolean(MainActivity.PREFERENCES_CHILD_MONITORED, false)
            .let { monitored ->
                viewModel.changeMonitoringStatus(monitored!!)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_child_view, container, false)

        tvChildName = view.findViewById(R.id.tvChildPhoneNumber)
        tvChildName.text = viewModel.userName

        //TODO ivChildPic = view.findViewById(R.id.ivChildPic) implement after pic adding
        //ivChildPic = view.findViewById(R.id.ivChildPic)

        btnStartMonitoring = view.findViewById(R.id.btnStartMonitoring)
        btnStartMonitoring.setOnClickListener {
            Log.d(TAG, "btnStartMonitoring onCLick")
            startJob()
        }

        btnStopMonitoring = view.findViewById(R.id.btnStopMonitoring)
        btnStopMonitoring.setOnClickListener {
            Log.d(TAG, "btnStopMonitoring onCLick")
            cancelJob()
        }

        ivIsMonitored = view.findViewById(R.id.ivIsChildMonitored)
        ivIsMonitored.setOnClickListener {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.child_is_being_monitored_msg),
                Toast.LENGTH_LONG
            ).show()

            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)?.let {
                it.findNavController()
                    .navigate(R.id.action_childViewFragment_to_watchSqlLocalDataBaseDebugFragment)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        appBar = (activity as AppCompatActivity).supportActionBar
        appBar?.let { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }

        viewModel.registerMonitoringChangeStatus(this)
        setIvIsMonitoredVisibility(viewModel.monitoringStatus)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unRegisterMonitoringChangeStatus()

        val sharedPref =
            activity?.getSharedPreferences(MainActivity.PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        sharedPref?.edit().let {
            it!!.putBoolean(MainActivity.PREFERENCES_CHILD_MONITORED, viewModel.monitoringStatus)
                .commit()
        }
    }

    private fun startJob() {
        if (checkUsageAccessesPermissions()) {
            if (checkPermissions())
                scheduleJob()
        } else {
            startActivity(
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            )
        }
    }

    private fun checkUsageAccessesPermissions(): Boolean {
        var appOpsManager: AppOpsManager?
        appOpsManager =
            requireContext().getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager

        var mode = 0
        mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            this.requireContext().packageName
        )

        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun checkPermissions(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }

//            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
//                Toast.makeText(context!!, "Require location permissions", Toast.LENGTH_SHORT).show()
//                return false
//            }

            else -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_LOCATION_CODE
                )
                return false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_LOCATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scheduleJob()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun scheduleJob() {
        Log.d(TAG, "scheduleJob")
        val cn = ComponentName(requireContext(), AppJobService::class.java)
        val info = JobInfo.Builder(JOB_ID, cn)
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//            .setPersisted(true)
            .setPeriodic(1000 * 60 * 15)
            .build()

        val js = activity?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val resultCode = js.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled")
            viewModel.changeMonitoringStatus(true)
        } else {
            Log.d(TAG, "Job not scheduled")
        }
    }

    private fun cancelJob() {
        Log.d(TAG, "cancelJob")
        val js = activity?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        js.cancel(JOB_ID)

        viewModel.changeMonitoringStatus(false)
    }

    private fun setIvIsMonitoredVisibility(isMonitored: Boolean) {
        ivIsMonitored.visibility = if (isMonitored) View.VISIBLE else View.GONE
    }

    override fun monitored(isMonitored: Boolean) {
        setIvIsMonitoredVisibility(isMonitored)
    }
}
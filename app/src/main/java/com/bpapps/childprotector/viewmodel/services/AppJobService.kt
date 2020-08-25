package com.bpapps.childprotector.viewmodel.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.bpapps.childprotector.App
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.ChildProtectorRepository
import com.bpapps.childprotector.view.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*


private const val TAG = "TAG.LocationJobService"
private const val UPDATE_INTERVAL_MINUTES: Int = 15

class AppJobService : JobService() {
    companion object {
        private var jobCanceled = false
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pm: PackageManager
    private val repository = ChildProtectorRepository.getInstance()

    override fun onCreate() {
        super.onCreate()
        pm = applicationContext.packageManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "LocationJobService started")

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val notification: Notification = Notification.Builder(this, App.CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_splash_screen)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(2357, notification)

        jobCanceled = false

        doBackgroundWork(params)

        return true
    }

    @SuppressLint("MissingPermission")
    private fun doBackgroundWork(params: JobParameters?) {
        Thread(Runnable {
            Log.d(TAG, "LocationJobService doBackgroundWork started")

            val currTime: Long = System.currentTimeMillis()

            if (jobCanceled)
                return@Runnable

            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    Log.d(TAG, "location : ${task.result?.latitude} , ${task.result?.longitude}")
                    repository?.addLocation(
                        Date(task.result?.time!!),
                        UUID.fromString(UUID.randomUUID().toString()),
                        task.result?.latitude!!,
                        task.result?.longitude!!
                    )
                } else {
                    Log.d(TAG, "getLastLocation:exception ${task.exception}")
                }
            }

            val usageStatsManager: UsageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val startTime = currTime - 1000 * 60 * UPDATE_INTERVAL_MINUTES
            val queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                currTime
            )
            Log.d(TAG, "Total ${queryUsageStats.size} queries")

            queryUsageStats.forEach { appStats ->
                if (appStats.totalTimeInForeground > 0) {
                    val packageName = appStats.packageName
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    val appName = pm.getApplicationLabel(appInfo)

                    Log.d(
                        TAG,
                        "query : packageName=${packageName}, appInfo=${appInfo}, appName=${appName}"
                    )
                }
            }

            jobFinished(params, false)

            Log.d(TAG, "LocationJobService doBackgroundWork finished")
        }).start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "LocationJobService onStopJob")
        jobCanceled = true
        return false
    }
}
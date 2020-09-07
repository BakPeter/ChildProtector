package com.bpapps.childprotector.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.AppUsageStatInterval

class UsageStatsAdapter(val dataSet: ArrayList<AppUsageStatInterval>) :
    RecyclerView.Adapter<UsageStatsAdapter.UsageStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageStatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usage_stats, parent, false)

        return UsageStatsViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: UsageStatsViewHolder, position: Int) {
        val us = dataSet[position]

        holder.tvAppName.text = us._appName
        holder.tvUsageTime.text = us._appUsageTime.toString()
        holder.tvCreator.text = us._appCreator
    }

    class UsageStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAppName: AppCompatTextView = view.findViewById(R.id.tvAppName)
        val tvUsageTime: AppCompatTextView = view.findViewById(R.id.tvUsageTime)
        val tvCreator: AppCompatTextView = view.findViewById(R.id.tvCreator)
    }
}
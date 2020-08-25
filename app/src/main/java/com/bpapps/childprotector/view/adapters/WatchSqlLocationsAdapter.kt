package com.bpapps.childprotector.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.Location

class WatchSqlLocationsAdapter(private val dataSet: List<Location>) :
    RecyclerView.Adapter<WatchSqlLocationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_shower, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = dataSet[position].toString()
        holder.tvLocationShower.text = location
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvLocationShower: AppCompatTextView = v.findViewById(R.id.tvLocation)

    }
}
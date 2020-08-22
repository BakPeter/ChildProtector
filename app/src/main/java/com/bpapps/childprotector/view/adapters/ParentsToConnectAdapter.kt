package com.bpapps.childprotector.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R

private const val TAG = "TAG.ConnectedParentsAdapter"

@SuppressLint("LongLogTag")
class ParentsToConnectAdapter(
    private val connectivityCodes: ArrayList<String>,
    private val context: Context
) :
    RecyclerView.Adapter<ParentsToConnectAdapter.ParentToConnectViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentToConnectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.parrent_to_connect_item, parent, false)

        return ParentToConnectViewHolder(view)
    }

    override fun getItemCount(): Int = connectivityCodes.size

    override fun onBindViewHolder(holder: ParentToConnectViewHolder, position: Int) {
        val code: String = connectivityCodes[position]

        holder.tvConnectivityCode.text = code
        holder.ivDeleteConnectivityCode.setOnClickListener {
            Toast.makeText(context, "Code $code has been deleted.", Toast.LENGTH_SHORT).show()
            connectivityCodes.removeAt(position)
            notifyDataSetChanged()
        }
    }

    class ParentToConnectViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvConnectivityCode: AppCompatTextView =
            v.findViewById(R.id.tvItemParentConnectivityCode)
        val ivDeleteConnectivityCode: AppCompatImageView =
            v.findViewById(R.id.imRemoveParentConnectivity)
    }

}
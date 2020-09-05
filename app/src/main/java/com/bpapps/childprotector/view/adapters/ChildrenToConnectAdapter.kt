package com.bpapps.childprotector.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.ChildToConnectInfo

class ChildrenToConnectAdapter(
    private val dataSet: ArrayList<ChildToConnectInfo>,
    private val context: Context
) :
    RecyclerView.Adapter<ChildrenToConnectAdapter.ChildrenToConnectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenToConnectViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_child_to_connect, parent, false)

        return ChildrenToConnectViewHolder(view)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ChildrenToConnectViewHolder, position: Int) {
        val dataItem = dataSet[position]

        holder.tvChildName.text = dataItem.name
        holder.tvChildPhoneNumber.text = dataItem.phoneNumber
        holder.ivDeleteChild.setOnClickListener {
            dataSet.remove(dataItem)
            notifyDataSetChanged()
        }
    }

    class ChildrenToConnectViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvChildName: AppCompatTextView = v.findViewById(R.id.tvChildName)
        val tvChildPhoneNumber: AppCompatTextView = v.findViewById(R.id.tvChildPhoneNumber)
        val ivDeleteChild: AppCompatImageView = v.findViewById(R.id.ivDeleteChild)
    }
}
package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.adapters.WatchSqlLocationsAdapter
import com.bpapps.childprotector.viewmodel.viewmodels.DataSqlDebugViewModel

class WatchSqlLocalDataBaseDebugFragment : Fragment() {
    private val viewModel by viewModels<DataSqlDebugViewModel>()

    private lateinit var rvLocations: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_watch_sql_local_data_base_debug, container, false)

        rvLocations = view.findViewById(R.id.rvLocations)
        rvLocations.layoutManager = LinearLayoutManager(requireContext())
        rvLocations.adapter = WatchSqlLocationsAdapter(viewModel.getLocations())

        return view
    }
}
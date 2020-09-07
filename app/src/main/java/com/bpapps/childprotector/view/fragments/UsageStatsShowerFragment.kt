package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.adapters.UsageStatsAdapter
import com.bpapps.childprotector.viewmodel.viewmodels.ParentViewViewModel


class UsageStatsShowerFragment(private val viewModel: ParentViewViewModel) : Fragment(),
    ParentViewViewModel.IOnCurrChildUpdated {

    private lateinit var rvUsageStats: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usage_stats_shower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rvUsageStats = view.findViewById(R.id.rvUsageStats)
        rvUsageStats.layoutManager = LinearLayoutManager(requireContext())
        rvUsageStats.adapter = UsageStatsAdapter(viewModel.childStats)
        rvUsageStats.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.registerForCurrChildChangedInd(this)
    }

    override fun onStop() {
        viewModel.unRegisterForCurrChildChangedInd()
        super.onStop()
    }

    override fun onUpdate(ind: Int) {
        rvUsageStats.adapter = UsageStatsAdapter(viewModel.childStats).also {
            it.notifyDataSetChanged()
        }
    }
}
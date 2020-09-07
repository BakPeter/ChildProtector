package com.bpapps.childprotector.view.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bpapps.childprotector.view.fragments.LocationsOnMapShowerFragment
import com.bpapps.childprotector.view.fragments.UsageStatsShowerFragment
import com.bpapps.childprotector.viewmodel.viewmodels.ParentViewViewModel

class ParentViewPagerAdapter(fragment: Fragment, private val viewModel: ParentViewViewModel) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                LocationsOnMapShowerFragment(viewModel)
            }

            else -> {//position = 1
                UsageStatsShowerFragment(viewModel)
            }
        }
    }


}
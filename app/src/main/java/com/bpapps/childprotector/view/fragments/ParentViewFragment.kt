package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.adapters.ParentViewPagerAdapter
import com.bpapps.childprotector.view.dialogs.WaiteDialog
import com.bpapps.childprotector.viewmodel.viewmodels.ParentViewViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ParentViewFragment : Fragment(), AdapterView.OnItemSelectedListener,
    ParentViewViewModel.IOnDataLoaded {

    private val viewModel by viewModels<ParentViewViewModel>()

    private var dialog: WaiteDialog? = null
    private lateinit var sChildChooser: Spinner
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sChildChooser = view.findViewById(R.id.sChildChooser)
        sChildChooser.onItemSelectedListener = this

        viewPager = view.findViewById(R.id.view_pager_2)
        viewPager.adapter = ParentViewPagerAdapter(this, viewModel)
        viewPager.isUserInputEnabled = false

        tabLayout = view.findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.locations)
                }
                1 -> {
                    tab.text = resources.getString(R.string.apps)
                }
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        if (!viewModel.allDataLoaded) {
            dialog = WaiteDialog(resources.getString(R.string.downloading_data)).also { it ->
                it.show(parentFragmentManager, null)
            }
        }

//        val appBar = (activity as AppCompatActivity).supportActionBar
//        appBar?.let { ab ->
//            ab.show()
//            ab.setDisplayHomeAsUpEnabled(false)
//        }

        viewModel.registerForDataLoadedCallback(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unRegisterDataLoadedCallback()
    }

    companion object {
        private const val TAG = "TAG.ParentViewFragment"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        Log.d(TAG, viewModel.children?.get(position).toString())
        viewModel.updateCurrChildViewedInd(position)
//            position
//        viewModel.updateCurrChildViewedInd(
//            position,
//            object : ParentViewViewModel.IOnCurrChildUpdated {
//                override fun onUpdate(ind: Int) {
//                    viewPager.adapter =
//                        ParentViewPagerAdapter(this@ParentViewFragment).also { adapter ->
//                            adapter.notifyDataSetChanged()
//                        }
//                }
//            })
    }

    override fun onLoaded() {
        dialog?.dismiss()
        dialog = null

        @SuppressLint("StaticFieldLeak")
        val task: AsyncTask<Void, Void, java.util.ArrayList<String>> =
            object : AsyncTask<Void, Void, ArrayList<String>>() {
                override fun doInBackground(vararg params: Void?): ArrayList<String> {
                    return viewModel.getChildrenNames()
                }

                override fun onPostExecute(result: ArrayList<String>?) {
                    ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        result!!.toList()
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sChildChooser.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        task.execute()
    }
}
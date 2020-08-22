package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bpapps.childprotector.R

/**
 * A simple [Fragment] subclass.
 * Use the [WatchSqlLocalDataBaseDebugFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WatchSqlLocalDataBaseDebugFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watch_sql_local_data_base_debug, container, false)
    }
}
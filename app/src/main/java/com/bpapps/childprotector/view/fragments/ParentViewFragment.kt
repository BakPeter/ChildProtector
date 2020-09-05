package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.MainActivity
import com.bpapps.childprotector.viewmodel.viewmodels.ParentViewViewModel


class ParentViewFragment : Fragment(), ParentViewViewModel.IUsersLoaded {

    private val viewModel by viewModels<ParentViewViewModel>()

    private lateinit var textView: AppCompatTextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_parent, container, false)
        textView = view.findViewById<AppCompatTextView>(R.id.tvSharedPrefTestShower)

        val sharedPref =
            activity?.getSharedPreferences(
                MainActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE
            )
        val isRegistered = sharedPref!!.getBoolean(MainActivity.PREFERENCES_IS_REGISTERED, false)
        val userType = sharedPref!!.getInt(MainActivity.PREFERENCES_USER_TYPE, -1)
        val userId =
            sharedPref.getString(MainActivity.PREFERENCES_USER_ID, "no user id in sharePref")

        val text: StringBuilder =
            StringBuilder("is register=$isRegistered'\n' userType=$userType'\n'userId=$userId")
        text.append("'\n'==================='\n'")

        return view
    }

    override fun onResume() {
        super.onResume()

        viewModel.registerForUserLoadedCallBack(this)

    }

    override fun onStop() {
        super.onStop()

        viewModel.unRegisterForUserLoadedCallBack()
    }

    override fun onLoaded() {
        val text: StringBuilder = StringBuilder(textView.text)
        text.append("\n=============================\n")

        val parent = viewModel.parent
        text.append("\nparent = ${parent.toString()}")
        text.append("\n=============================\n")

        val children = viewModel.children
        children?.forEachIndexed { index, user ->
            text.append("\n#$index : $user\n")
        }

        textView.text = text
    }
}
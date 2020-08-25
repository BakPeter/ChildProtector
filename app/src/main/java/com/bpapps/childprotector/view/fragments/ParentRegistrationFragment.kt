package com.bpapps.childprotector.view.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.view.dialogs.ClearInputDataDialog
import com.bpapps.childprotector.view.dialogs.ExitAppDialog
import com.bpapps.childprotector.viewmodel.viewmodels.ParentRegistrationViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ParentRegistrationFragment : Fragment() {

    private val viewModel by viewModels<ParentRegistrationViewModel>()

    private lateinit var etParentPhoneNumber: AppCompatEditText
    private lateinit var rvChildrenToConnect: RecyclerView
    private lateinit var btnParentRegister: AppCompatButton
    private lateinit var fabAddUser: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent_registration, container, false)

        etParentPhoneNumber = view.findViewById(R.id.etParentPhoneNumber)

        rvChildrenToConnect = view.findViewById(R.id.rvChildrenToConnect)

        btnParentRegister = view.findViewById(R.id.btnParentRegister)
        btnParentRegister.setOnClickListener {
            Toast.makeText(requireContext(), "register parent", Toast.LENGTH_SHORT).show()
        }

        fabAddUser = view.findViewById(R.id.fabAddUser)
        fabAddUser.setOnClickListener {
            Toast.makeText(requireContext(), "add parent +", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.also { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.registration_parent_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_clear_input -> {
                clearInput()
                true
            }

            R.id.menu_action_settings -> {
                Toast.makeText(requireContext(), "settings", Toast.LENGTH_SHORT).show()
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun clearInput(): Boolean {
        var funRetVal = true
        ClearInputDataDialog(
            resources.getString(R.string.clear_input_title),
            resources.getString(R.string.clear_input_msg),
            null,
            object : ExitAppDialog.IOnCLickCallBack {
                override fun onClick(retVal: Boolean) {
                    funRetVal = retVal
                    if (retVal) {
                        clearAllInputData()
                    }
                }
            }
        ).let {
            it.show(parentFragmentManager, null)
        }

        return funRetVal
    }

    private fun clearAllInputData() {
        //TODO private fun clearAllInputData()
    }
}
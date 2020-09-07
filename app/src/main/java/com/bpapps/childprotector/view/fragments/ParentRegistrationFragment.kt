package com.bpapps.childprotector.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.ChildToConnectInfo
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.model.classes.UserType
import com.bpapps.childprotector.view.MainActivity
import com.bpapps.childprotector.view.adapters.ChildrenToConnectAdapter
import com.bpapps.childprotector.view.dialogs.*
import com.bpapps.childprotector.viewmodel.viewmodels.ParentRegistrationViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ParentRegistrationFragment : Fragment(),
    ParentRegistrationViewModel.IChildrenToConnectInfoUpdated {

    private val viewModel by viewModels<ParentRegistrationViewModel>()

    private lateinit var etParentPhoneNumber: AppCompatEditText
    private lateinit var etParentName: AppCompatEditText
    private lateinit var rvChildrenToConnect: RecyclerView
    private lateinit var tvAddChildMsg: AppCompatTextView
    private lateinit var btnParentRegister: AppCompatButton
    private lateinit var fabAddUser: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parent_registration, container, false)

        etParentPhoneNumber = view.findViewById(R.id.etParentPhoneNumber)
        etParentName = view.findViewById(R.id.etParentName)

        tvAddChildMsg = view.findViewById(R.id.tvAddChildMsg)

        rvChildrenToConnect = view.findViewById(R.id.rvChildrenToConnect)
        rvChildrenToConnect.layoutManager = LinearLayoutManager(requireContext())
        rvChildrenToConnect.adapter =
            ChildrenToConnectAdapter(viewModel.getChildrenToConnectInfo(), requireContext())

        btnParentRegister = view.findViewById(R.id.btnParentRegister)
        btnParentRegister.setOnClickListener {
            val dialog = WaiteDialog(resources.getString(R.string.registration_in_progress))
            dialog.show(parentFragmentManager, null)

            val name = etParentName.text.toString()
            val phoneNumber = etParentPhoneNumber.text.toString()

            viewModel.parentName = name
            viewModel.parentPhoneNumber = phoneNumber

            viewModel.registerParent(
                object : ParentRegistrationViewModel.IOnRegistrationFailure {
                    override fun onFailure(errorType: Int) {
                        ErrorDialog(getErrorMessage(errorType)).show(
                            parentFragmentManager,
                            null
                        )
                    }
                },
                object : ParentRegistrationViewModel.IOnRegistrationSuccess {
                    override fun onSuccess(parent: User, children: ArrayList<User>) {
                        val activity = activity
                        activity?.getSharedPreferences(
                            MainActivity.PREFERENCES_FILE_NAME,
                            Context.MODE_PRIVATE
                        )?.edit()?.let { editor ->
                            editor!!.putBoolean(MainActivity.PREFERENCES_IS_REGISTERED, true)
                                .putInt(MainActivity.PREFERENCES_USER_TYPE, UserType.PARENT)
                                .putString(MainActivity.PREFERENCES_USER_ID, viewModel.parentId)
                                .commit()
                        }

                        dialog.dismiss()
//                        Toast.makeText(
//                            requireContext(),
//                            resources.getString(R.string.registration_finished),
//                            Toast.LENGTH_SHORT
//                        ).show()

                        activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)
                            ?.findNavController()
                            ?.popBackStack(R.id.splashScreenFragment, false)
                    }
                })
        }

        fabAddUser = view.findViewById(R.id.fabAddUser)
        fabAddUser.setOnClickListener {
            AddChildDialog(object : AddChildDialog.IChildInfoAdded {
                override fun childAdded(childInfo: ChildToConnectInfo) {
                    viewModel.addChildInfo(childInfo)
                }
            }).show(parentFragmentManager, null)
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

        viewModel.registerForChildrenToConnectDataSetChanges(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.unRegisterForChildrenToConnectDataSetChanges()
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

            R.id.menu_action_add_debug_data -> {
                viewModel.addChildInfo(ChildToConnectInfo("AAA", "111"))
                viewModel.addChildInfo(ChildToConnectInfo("BBB", "222"))

                return true
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
        viewModel.clearAllChildrenData()
        etParentPhoneNumber.text?.clear()
        etParentName.text?.clear()
    }

    override fun dataUpdated(newDataSet: ArrayList<ChildToConnectInfo>) {
        rvChildrenToConnect.adapter?.notifyDataSetChanged()
    }

    private fun getErrorMessage(errorType: @ErrorType Int): String? {
        return when (errorType) {
            ErrorType.NOUN ->
                null

            ErrorType.MISSING_PARENT_NAME ->
                resources.getString(R.string.missing_parent_name)

            ErrorType.MISSING_PARENT_PHONE_NUMBER ->
                resources.getString(R.string.missing_parent_phone_number)

            ErrorType.ENTER_AT_LEAST_ONE_CHILD ->
                resources.getString(R.string.enter_at_least_one_child)

            else ->
                null
        }
    }
}
package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.ChildProtectorException
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.model.classes.UserType
import com.bpapps.childprotector.view.MainActivity
import com.bpapps.childprotector.view.adapters.ParentsToConnectAdapter
import com.bpapps.childprotector.view.dialogs.*
import com.bpapps.childprotector.viewmodel.viewmodels.ChildRegistrationViewModel

private const val TAG = "TAG.ChildRegistrationFragment"

@SuppressLint("LongLogTag")
class ChildRegistrationFragment : Fragment(),
    ChildRegistrationViewModel.IParentConnectivityCodesUpdated {

    private val viewModel by viewModels<ChildRegistrationViewModel>()

    private lateinit var etChildPhoneNumber: AppCompatEditText
    private lateinit var rvParentsToConnect: RecyclerView
    private lateinit var btnChildRegister: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_child_registration, container, false)

        etChildPhoneNumber = view.findViewById(R.id.etParentPhoneNumber)

        rvParentsToConnect = view.findViewById(R.id.rvChildrenToConnect)
        rvParentsToConnect.layoutManager = LinearLayoutManager(context)
        rvParentsToConnect.adapter =
            ParentsToConnectAdapter(viewModel.getParentsCodes(), requireContext())

        btnChildRegister = view.findViewById(R.id.btnParentRegister)
        btnChildRegister.setOnClickListener { _ ->

            val dialog = WaiteDialog(resources.getString(R.string.registration_in_progress))
            dialog.show(parentFragmentManager, null)

            val childPhoneNumber = etChildPhoneNumber.text
            viewModel.setChildPhoneNumber(childPhoneNumber.toString())

            viewModel.registerChild(
                object : ChildRegistrationViewModel.IChildRegistrationSuccess {
                    override fun onSuccesses(child: User, parents: ArrayList<User>) {
                        val sharedPref = activity?.getSharedPreferences(
                            MainActivity.PREFERENCES_FILE_NAME,
                            Context.MODE_PRIVATE
                        )

                        sharedPref?.edit().let {
                            it!!.putBoolean(MainActivity.PREFERENCES_IS_REGISTERED, true)
                                .putInt(MainActivity.PREFERENCES_USER_TYPE, UserType.CHILD)
                                .commit()
                        }

                        dialog.dismiss()

`                        val navHostFragment =
                            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)
                        navHostFragment?.findNavController()
                            ?.popBackStack(R.id.splashScreenFragment, false)
                    }
                },
                object : ChildRegistrationViewModel.IChildRegistrationFailure {
                    override fun onFailure(error: ChildProtectorException) {
                        dialog.dismiss()
                        ErrorDialog(getErrorMessage(error.errorType)).show(
                            parentFragmentManager,
                            null
                        )
                    }
                })
        }

        return view
    }

    private fun getErrorMessage(errorType: @ErrorType Int): String? {
        return when (errorType) {
            ErrorType.NOUN ->
                "NOUN"
            ErrorType.MISSING_AT_LEAST_ONE_PARENT_CODE ->
                resources.getString(R.string.missing_at_least_one_parent_code)
            ErrorType.MISSING_CHILD_PHONE_NUMBER ->
                resources.getString(R.string.missing_user_phone_number)
            ErrorType.DEBUG ->
                "DEBUG"
            ErrorType.USER_NOT_IN_THE_DATA_BASE ->
                resources.getString(R.string.parent_connectivity_code_not_in_the_data_base)
            ErrorType.ERROR_LOADING_CONNECTED_USER ->
                resources.getString(R.string.error_loading_connected_user)
            ErrorType.EXTERNAL_ERROR ->
                "EXTERNAL_ERROR"
            ErrorType.CONNECTIVITY_CODE_NOT_IN_THE_DATA_BASE ->
                resources.getString(R.string.connectivity_code_not_in_the_data_base)
            ErrorType.PHONE_NOT_IN_THE_DATA_BASE ->
                resources.getString(R.string.phone_not_in_the_data_base)
            else ->
                null
        }
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.also { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }

        viewModel.registerParentsCodeChangedCallBack(this)
    }

    override fun onStop() {
        super.onStop()

        viewModel.unRegisterParentsCodeChangedCallBack()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.registration_child_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_debug -> {
                viewModel.addParentCode(viewModel.debugConnectivityCode)
//                etChildPhoneNumber.setText(viewModel.debugConnectivityCode)
                true
            }

            R.id.menu_action_clear_input -> {
                clearInput()
                true
            }

            R.id.menu_action_add_parent -> {
//                Toast.makeText(context, "menu_action_add_parent", Toast.LENGTH_SHORT).show()
//                Log.d(TAG, "menu_action_add_parent")
                AddParentCodeDialog(object : AddParentCodeDialog.IOnParentCodeEntered {
                    override fun onCodeEntered(code: String) {
//                        Log.d(TAG, "code = $code")
                        viewModel.addParentCode(code)
                    }
                }).show(parentFragmentManager, null)

                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun clearInput() {
        ClearInputDataDialog(
            "Clear input?",
            "Do you want to clear the entire input?",
            null,
            object : ExitAppDialog.IOnCLickCallBack {
                override fun onClick(retVal: Boolean) {
                    if (retVal) {
                        clearAllInputData()
                    }
                }
            }
        ).let {
            it.show(parentFragmentManager, null)
        }
    }

    private fun clearAllInputData() {
        viewModel.clearParentsCodes(this@ChildRegistrationFragment)
        etChildPhoneNumber.text?.clear()
//        Toast.makeText(context, "Data Cleared", Toast.LENGTH_SHORT).show()
    }

    override fun codesUpdated(codes: ArrayList<String>) {
        rvParentsToConnect.adapter?.notifyDataSetChanged()
    }
}
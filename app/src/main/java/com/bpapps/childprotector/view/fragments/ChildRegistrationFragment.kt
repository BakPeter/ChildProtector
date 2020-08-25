package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.ErrorType
import com.bpapps.childprotector.model.classes.UserType
import com.bpapps.childprotector.view.MainActivity
import com.bpapps.childprotector.view.adapters.ParentsToConnectAdapter
import com.bpapps.childprotector.view.dialogs.AddParentCodeDialog
import com.bpapps.childprotector.view.dialogs.ClearInputDataDialog
import com.bpapps.childprotector.view.dialogs.ErrorDialog
import com.bpapps.childprotector.view.dialogs.ExitAppDialog
import com.bpapps.childprotector.viewmodel.viewmodels.ChildRegistrationViewModel

private const val TAG = "TAG.ChildRegistrationFragment"

@SuppressLint("LongLogTag")
class ChildRegistrationFragment : Fragment(),
    ChildRegistrationViewModel.IParentConnectivityCodesUpdated {

    private val viewModel by viewModels<ChildRegistrationViewModel>()

    private lateinit var _etChildPhoneNumber: AppCompatEditText
    private lateinit var _rvParentsToConnect: RecyclerView
    private lateinit var _pbChildRegistration: ProgressBar
    private lateinit var _btnChildRegister: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_child_registration, container, false)

        _etChildPhoneNumber = view.findViewById(R.id.etParentPhoneNumber)

        _rvParentsToConnect = view.findViewById(R.id.rvChildrenToConnect)
        _rvParentsToConnect.layoutManager = LinearLayoutManager(context)
        _rvParentsToConnect.adapter =
            ParentsToConnectAdapter(viewModel.getParentsCodes(), requireContext())

        _pbChildRegistration = view.findViewById(R.id.pbParentRegistration)

        _btnChildRegister = view.findViewById(R.id.btnParentRegister)
        _btnChildRegister.setOnClickListener { _ ->
            val childPhoneNumber = _etChildPhoneNumber.text
            viewModel.setChildPhoneNumber(childPhoneNumber.toString())

            val registerResult: ErrorType = viewModel.registerChild()

            viewModel.registerChild().let { it ->
                if (it.type == ErrorType.NOUN) {
                    val sharedPref = activity?.getSharedPreferences(
                        MainActivity.PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE
                    )

//                    Handler().post {
//                        sharedPref?.edit().let { it ->
//                            it!!.putBoolean(MainActivity.PREFERENCES_IS_REGISTERED, true)
//                                .putInt(MainActivity.PREFERENCES_USER_TYPE, UserType.CHILD)
//                                .commit()
//                        }
//                    }

                    sharedPref?.edit().let {
                        it!!.putBoolean(MainActivity.PREFERENCES_IS_REGISTERED, true)
                            .putInt(MainActivity.PREFERENCES_USER_TYPE, UserType.CHILD)
                            .commit()
                    }
                    val navHostFragment =
                        activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment)
                    navHostFragment?.findNavController()
                        ?.popBackStack(R.id.splashScreenFragment, false)
                } else {
                    ErrorDialog(getErrorMessage(registerResult)).show(parentFragmentManager, null)
                }
            }
        }

        return view
    }

    private fun getErrorMessage(errorType: ErrorType): String? {
        return when (errorType.type) {
            ErrorType.NOUN ->
                null
            ErrorType.MISSING_AT_LEAST_ONE_PARENT_CODE ->
                resources.getString(R.string.missing_at_least_one_parent_code)
            ErrorType.MISSING_USER_PHONE_NUMBER ->
                resources.getString(R.string.missing_user_phone_number)
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
            R.id.menu_action_clear_input -> {
                clearInput()
                true
            }

            R.id.menu_action_add_parent -> {
//                Toast.makeText(context, "menu_action_add_parent", Toast.LENGTH_SHORT).show()
//                Log.d(TAG, "menu_action_add_parent")
                AddParentCodeDialog(object : AddParentCodeDialog.IOnParentCodeEntered {
                    override fun onCodeEntered(code: String) {
                        Log.d(TAG, "code = $code")
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

    private fun clearInput(): Boolean {
        var funRetVal = true
        ClearInputDataDialog(
            "Clear input?",
            "Do you want to clear the entire input?",
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
        viewModel.clearParentsCodes(this@ChildRegistrationFragment)
        _etChildPhoneNumber.text?.clear()
        Toast.makeText(context, "Data Cleared", Toast.LENGTH_SHORT).show()
    }

    override fun codesUpdated(codes: ArrayList<String>) {
        _rvParentsToConnect.adapter?.notifyDataSetChanged()
    }
}
package com.bpapps.childprotector.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.model.classes.User
import com.bpapps.childprotector.view.dialogs.ExitAppDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "TAG.RegistrationFragment"

class RegistrationFragment : Fragment() {

    //    private var actionBar: ActionBar? = null
    private lateinit var etUserPhoneNumber: AppCompatEditText
    private lateinit var etUserName: AppCompatEditText
    private lateinit var rgUserType: RadioGroup
    private lateinit var tvAddConnectedUserMsgShower: AppCompatTextView
    private lateinit var rvChosenUsersShower: RecyclerView
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var btnRegister: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*actionBar =*/
        (activity as AppCompatActivity).supportActionBar?.also { ab ->
            ab.show()
            ab.setDisplayHomeAsUpEnabled(false)
        }

        (activity as AppCompatActivity).onBackPressedDispatcher.addCallback(this) {
            val resourcesCpy = resources

            ExitAppDialog(
                resourcesCpy.getString(R.string.exit_dialog_title),
                resourcesCpy.getString(R.string.exit_dialog_message),
                R.drawable.ic_splash_screen,
                object : ExitAppDialog.IOnCLickCallBack {
                    override fun onClick(cancelled: Boolean) {
                        if (cancelled) {
                            (activity as AppCompatActivity).finish()
                        }
                    }
                }
            ).let { it ->
                it.show(parentFragmentManager, null)
            }
        }
    }

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        etUserPhoneNumber = view.findViewById(R.id.etUserPhoneNumber) as AppCompatEditText
        etUserName = view.findViewById(R.id.etUserName) as AppCompatEditText
        rgUserType = view.findViewById(R.id.rgUserType) as RadioGroup
//        rgUserType.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
////            val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
//            val checkedRadioButton: RadioButton =
//                group.findViewById(rgUserType.checkedRadioButtonId)
//            Log.d(TAG, checkedRadioButton.text.toString())
//        })
//
        tvAddConnectedUserMsgShower = view.findViewById(R.id.tvAddConnectedUserMsgShower)
        tvAddConnectedUserMsgShower.text =
            resources.getString(R.string.tvAddConnectedUserMsg, User.MAX_CONNECTED_USER)


        return view
    }
}
package com.bpapps.childprotector.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.bpapps.childprotector.R
import com.bpapps.childprotector.viewmodel.ChildProtectorViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var tvUserName: AppCompatTextView
    private val viewModel by viewModels<ChildProtectorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvUserName = findViewById(R.id.tv_userName)
//        tvUserName.text = viewModel.getUserName() + viewModel.getCounter()
        tvUserName.text = viewModel.getTVUserNameText()

        tvUserName.setOnClickListener {
            Toast.makeText(this, "tvUserName::onClickListener", Toast.LENGTH_SHORT).show()
//            tvUserName.text =
//                if (tvUserName.text == null) counter.toString() else tvUserName.text.toString() + counter

            viewModel.upgradeCounter()

            tvUserName.text = viewModel.getTVUserNameText()
        }
    }
}
